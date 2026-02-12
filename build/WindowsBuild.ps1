$location = Get-Location

Write-Host "Checking if Java Exists..."
$javaExists = (Get-Command java | Select-Object -ExpandProperty Version).tostring()

$runPath = $PSScriptRoot
cd $runPath/..

if($javaExists -ne ""){
    Write-Host "Java Found..."
    Write-Host "Searching for Command..."
    if ($args.Length -eq 0){
	Write-Host "No command found, expected 1 argument..."
	Write-Host "Example of command can be 'build', 'publish' or 'clean'"
    } else {
	$command = $args[0]
	Write-Host "Command is $command"
	if($command -eq "build"){
	    $srcRoot = Get-Location
	    if(Test-Path -Path build\BuildList.txt){
		Remove-Item -Force build\BuildList.txt
	    }
	    New-Item -Path "build\BuildList.txt" -ItemType File
	    Get-ChildItem -Path "$srcRoot/src" -Recurse -File | Where-Object { $_.Name -notlike "#*" -and $_.Name -notlike "*~" } | Select-Object -ExpandProperty FullName > build\BuildList.txt
	    foreach ($line in Get-Content -Path 'build\BuildList.txt') {
		$content = Get-Content -Path $line -Raw
    
		# Write the content back without BOM using .NET method
		$Utf8NoBomEncoding = New-Object System.Text.UTF8Encoding $False
		[System.IO.File]::WriteAllText($line, $content, $Utf8NoBomEncoding)
	    }
	    cat "build/BuildList.txt"
	    javac "@build/BuildList.txt" -d "./bin" -sourcepath "./src" -cp "lib\openjfx-25.0.2_windows-x64_bin-sdk\javafx-sdk-25.0.2\lib\*;./lib/*" -encoding "UTF-8"
	    $tmp = "./tmp"
	    $dependencyJars = Get-ChildItem -Path lib -Filter *.jar
	    foreach ($jar in $dependencyJars) {
		# Extract contents of each dependency JAR into the temp directory
		jar xf $jar.FullName -C $tmp
	        if (Test-Path ".\tmp\META-INF\MANIFEST.MF") {
		    Remove-Item ".\tmp\META-INF\MANIFEST.MF"
		}
		# Move extracted files to classes directory, merging them
		Move-Item -Path "$tmp\*" -Destination "./bin" -ErrorAction SilentlyContinue
	    }
	    Remove-Item -Path $tmp/* -Recurse -Force
	    jar cf "$tmp/EdeStl.jar" "./bin"
	    Remove-Item -Path ./bin/* -Recurse -Force
	    Move-Item "./tmp/EdeStl.jar" "./bin"
	} elseif ($command -eq "clean"){
	    Get-ChildItem -Path './src' -Include *.class -Recurse | Remove-Item -Force
	    Get-ChildItem -Path './bin' -Include *.class -Recurse | Remove-Item -Force
	    Remove-Item -Recurse -Force "*~"
	    Remove-Item -Recurse -Force "*#" 
	} else {
	    Write-Host "Unknown command '$command'"
	}
    }
}

# At end of script go back to current location
cd $location

