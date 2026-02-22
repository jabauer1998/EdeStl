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
	    Write-Host "Building Src"
	    javac "@build/BuildList.txt" -d "./tmp" -sourcepath "./src" -cp "./lib/*" -encoding "UTF-8"
			jar xf "./lib/asm-9.6.jar "-C "./tmp"
	    Write-Host "Bundling into a Jar"
	    jar cf "./bin/EdeStl.jar" -C "./tmp" "."
	    Write-Host "Deleting Tmp Directory"
	    Remove-Item -Path ./tmp/* -Recurse -Force
	    javac "sample/ede/Processor.java" -d "./tmp" -sourcepath "./sample" -cp "./bin/EdeStl.jar" -encoding "UTF-8"
	    jar cfe "./bin/EdeSample.jar" "sample.ede.Processor.java" -C "./tmp" "."
	    Remove-Item -Path "./tmp/*" -Recurse -Force
	} elseif ($command -eq "run") {
	    java -jar ./bin/EdeSample.jar
	} elseif ($command -eq "clean"){
	    Get-ChildItem -Path './src' -Include *.class -Recurse | Remove-Item -Force
	    Get-ChildItem -Path './bin' -Include * -Recurse | Remove-Item -Force
	    Get-ChildItem -Path './tmp' -Include * -Recurse | Remove-Item -Force
	    Remove-Item -Recurse -Force "*~"
	    Remove-Item -Recurse -Force "*#" 
	} else {
	    Write-Host "Unknown command '$command'"
	}
    }
}

# At end of script go back to current location
cd $location

