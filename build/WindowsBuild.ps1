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
	    Get-ChildItem -Path "$srcRoot/src" -Recurse -File | Select-Object -ExpandProperty FullName > build\BuildList.txt
	    Get-Content -Path 'build\BuildList.txt' | Set-Content -Path 'build.BuildList.txt' -Encoding utf8
	    foreach ($line in Get-Content -Path 'build\BuildList.txt') {
		New-Item -Path "$line.out" -ItemType File
		Get-Content -Path $line | Set-Content -Path "$line.out" -Encoding utf8
		mv "$line.out" $line
	    }
	    cat "build/BuildList.txt"
	    javac "@build/BuildList.txt" -encoding "UTF-8"
	} elseif ($command -eq "clean"){
	    Remove-Item -Recurse bin/*
	} elseif ($command -eq "publish"){

	} else {
	    Write-Host "Unknown command '$command'"
	}
    }
}

# At end of script go back to current location
cd $location

