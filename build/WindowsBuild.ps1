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
	    javac "@build/BuildList.txt" -sourcepath "./src" -d "./tmp" -cp ".\lib\*" -encoding "UTF-8"
	    jar cf "bin/EdeStl.jar" -C "./tmp/" "."
	    Remove-Item -Path "./tmp/*" -Recurse -Force
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

