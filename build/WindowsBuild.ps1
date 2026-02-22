$location = Get-Location

$runPath = $PSScriptRoot
cd $runPath/..

$JDK25_HOME = "$(Get-Location)\tools\jdk-25.0.2"
if (Test-Path -Path $JDK25_HOME) {
    $env:JAVA_HOME = $JDK25_HOME
    $env:Path = "$JDK25_HOME\bin;$env:Path"
}

Write-Host "Checking if Java Exists..."
$javaExists = (java -version 2>&1 | Select-Object -First 1).ToString()

if($javaExists -ne ""){
    Write-Host "Java Found: $javaExists"
    Write-Host "Searching for Command..."
    if ($args.Length -eq 0){
        Write-Host "No command found, expected 1 argument..."
        Write-Host "Example of command can be 'build', 'publish', 'run' or 'clean'"
    } else {
        $command = $args[0]
        Write-Host "Command is $command"
        if($command -eq "build"){
            $srcRoot = Get-Location
            if(Test-Path -Path build\BuildList.txt){
                Remove-Item -Force build\BuildList.txt
            }
            New-Item -Path "build\BuildList.txt" -ItemType File | Out-Null
            Get-ChildItem -Path "$srcRoot/src" -Recurse -File | Where-Object { $_.Name -notlike "#*" -and $_.Name -notlike "*~" } | Select-Object -ExpandProperty FullName > build\BuildList.txt
            foreach ($line in Get-Content -Path 'build\BuildList.txt') {
                $content = Get-Content -Path $line -Raw
                $Utf8NoBomEncoding = New-Object System.Text.UTF8Encoding $False
                [System.IO.File]::WriteAllText($line, $content, $Utf8NoBomEncoding)
            }
            cat "build/BuildList.txt"

            $CLASSPATH = ""
            foreach ($jar in Get-ChildItem -Path "lib\*.jar" -ErrorAction SilentlyContinue) {
                if ($CLASSPATH -ne "") {
                    $CLASSPATH += ";"
                }
                $CLASSPATH += $jar.FullName
            }

            if (-not (Test-Path -Path "tmp")) { New-Item -Path "tmp" -ItemType Directory | Out-Null }
            if (-not (Test-Path -Path "bin")) { New-Item -Path "bin" -ItemType Directory | Out-Null }

            Write-Host "Building Src"
            if ($CLASSPATH -ne "") {
                javac "@build/BuildList.txt" -sourcepath "./src" -classpath "$CLASSPATH" -d "tmp" -encoding "UTF-8"
            } else {
                javac "@build/BuildList.txt" -sourcepath "./src" -d "tmp" -encoding "UTF-8"
            }
            if ($LASTEXITCODE -ne 0) {
                Write-Host "Build failed!"
                cd $location
                exit 1
            }

            if (Test-Path -Path "./lib/asm-9.6.jar") {
                Write-Host "Extracting asm-9.6.jar into tmp"
                Push-Location tmp
                jar xf "../lib/asm-9.6.jar"
                Pop-Location
            }

            Write-Host "Bundling into a Jar"
            jar cf "./bin/EdeStl.jar" -C "./tmp" "."

            Write-Host "Deleting Tmp Directory"
            Remove-Item -Path ./tmp/* -Recurse -Force

            if (Test-Path -Path "sample/ede/Processor.java") {
                Write-Host "Building Sample"
                $SAMPLE_CP = "./bin/EdeStl.jar"
                foreach ($jar in Get-ChildItem -Path "lib\*.jar" -ErrorAction SilentlyContinue) {
                    if ($jar.Name -eq "asm-9.6.jar") { continue }
                    $SAMPLE_CP += ";$($jar.FullName)"
                }
                javac "sample/ede/Processor.java" -d "./tmp" -sourcepath "./sample" -cp "$SAMPLE_CP" -encoding "UTF-8"
                if ($LASTEXITCODE -eq 0) {
                    Write-Host "Bundling Sample into a Jar"
                    jar cfe "./bin/EdeSample.jar" "sample.ede.Processor" -C "./tmp" "."
                } else {
                    Write-Host "Sample build failed (non-fatal), skipping sample jar"
                }
                Remove-Item -Path ./tmp/* -Recurse -Force
            }
        } elseif ($command -eq "run") {
            java -jar ./bin/EdeSample.jar
        } elseif ($command -eq "clean"){
            Get-ChildItem -Path './src' -Include *.class -Recurse -ErrorAction SilentlyContinue | Remove-Item -Force
            if (Test-Path -Path './bin') { Remove-Item -Path './bin/*' -Recurse -Force -ErrorAction SilentlyContinue }
            if (Test-Path -Path './tmp') { Remove-Item -Path './tmp/*' -Recurse -Force -ErrorAction SilentlyContinue }
            Get-ChildItem -Path '.' -Include "*~" -Recurse -ErrorAction SilentlyContinue | Remove-Item -Force
            Get-ChildItem -Path '.' -Include "*#" -Recurse -ErrorAction SilentlyContinue | Remove-Item -Force
        } elseif ($command -eq "publish") {
            # placeholder
        } else {
            Write-Host "Unknown command '$command'"
        }
    }
}

cd $location
