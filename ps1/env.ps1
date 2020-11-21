
#
# JAVA_HOME environment variable must be set either externally in the Poweshell
# environment or internally here by uncommenting out the Set-Variable line
# below and assiging it the location of a valid JDK 15 runtime.
#
#$env:JAVA_HOME = 'D:\openjdk\jdk-15'

#
# Unless these script files have been deliberately moved, the parent
# directory of the directory containining these script files houses
# the maven project and source code.
#
Set-Variable -Name PROJECTDIR -Value ".."

#
# native platform
#
Set-Variable -Name PLATFORM -Value win

#
# Application specific variables
#
Set-Variable -Name PROJECT -Value cgminerspi
Set-Variable -Name VERSION -Value "4.10.0"
Set-Variable -Name MAINCLASS -Value com.jtconnors.cgminerapi.Samples
Set-Variable -Name MAINJAR -Value $PROJECT-$VERSION.jar

#
# Local maven repository for jars
#
Set-Variable -Name REPO -Value $HOME\.m2\repository

#
# Directory under which maven places compiled classes and built jars
#
Set-Variable -Name TARGET -Value target

#
# Required external modules for this application
#
Set-Variable -Name EXTERNAL_CLASSPATH -Value @(
    "$REPO\javax\json\javax.json.api\1.1.4\javax.json-api-1.1.4.jar",
    "$REPO\org\glassfish\javax.json\1.1\javax.json-1.1.jar"
)

#
# Create a CLASSPATH for the java command.  It either includes the classes
# in the $TARGET directory or the $TARGET/$MAINJAR (if it exists) and the
# $EXTERNAL_CLASSPATH defined in env.ps1.
#
if (Test-Path $PROJECTDIR\$TARGET\$MAINJAR) {
    Set-Variable -Name CLASSPATH -Value $TARGET\$MAINJAR
} else {
     Set-Variable -Name CLASSPATH -Value $TARGET\classes
}
ForEach ($i in $EXTERNAL_CLASSPATH) {
   $CLASSPATH += ";"
   $CLASSPATH += $i
}

Set-Variable -Name SCRIPT_NAME -Value $MyInvocation.MyCommand.Name

#
# Function to print command-line options to standard output
#
function Print-Options {
    Write-Output "usage: ${SCRIPT_NAME} [-?,--help,-e,-n,-v]"
    Write-Output "  -? or --help - print options to standard output and exit"
    Write-Output "  -e - echo the jdk command invocations to standard output"
    Write-Output "  -n - don't run the java commands, just print out invocations"
    Write-Output "  -v - --verbose flag for jdk commands that will accept it"
}

#
# Process command-line arguments:  Not all flags are valid for all invocations,
# but we'll parse them anyway.
#
#   -? or --help  print options to standard output and exit
#   -e	echo the jdk command invocations to standard output
#   -n  don't run the java commands, just print out invocations
#   -v 	--verbose flag for jdk commands that will accept it
#
Set-Variable -Name VERBOSE_OPTION -Value $null
Set-Variable -Name ECHO_CMD -Value false
Set-Variable -Name EXECUTE_OPTION -Value true
Set-Variable -Name JUST_EXIT -Value false -Scope Global


Foreach ($arg in $CMDLINE_ARGS) {
    switch ($arg) {
        '-?' {
            Print-Options
            Set-Variable -Name JUST_EXIT -Value true -Scope Global 
        }
        '--help' {
            Print-Options
            Set-Variable -Name JUST_EXIT -Value true -Scope Global
        }
        '-e' { 
            Set-Variable -Name ECHO_CMD -Value true   
        }
        '-n' { 
            Set-Variable -Name ECHO_CMD -Value true
            Set-Variable -Name EXECUTE_OPTION -Value false   
        }
        '-v' {
            Set-Variable -Name VERBOSE_OPTION -Value "--verbose"
        }
        default {
            Write-Output "${SCRIPT_NAME}: bad option '$arg'"
            Print-Options
            Set-Variable -Name JUST_EXIT -Value true -Scope Global
        }
    }
}

#
# Print a command with all its args on one line. 
#
function Print-Cmd {
    Write-Output ""
    Foreach ($item in $args[0]) {
       $CMD += $item
       $CMD += " "
    }
    Write-Output $CMD
}

#
# Function to print out an error message and exit with exitcode
#
function GoodBye($MSG, $EXITCODE) {
   Write-Output $MSG
   Set-Variable -Name JUST_EXIT -Value true -Scope Global
   Exit $EXITCODE    
}

#
# Function to execute command specified by arguments.
# If $ECHO_CMD is true then print the command out to standard output first.
# If $EXECUTE_OPTION is set to anything other than "true", then don't execute
# command, just print it out.
#
function Exec-Cmd {
    Set-Variable -Name OPTIONS -Value @()
    $COMMAND = $($args[0][0])
    Foreach ($item in $args[0][1]) {
       $OPTIONS += $item
    }
    if ($ECHO_CMD -eq "true") {
        Print-Cmd ($COMMAND, $OPTIONS)
    }
    if ($EXECUTE_OPTION -eq "true") {
        & $COMMAND $OPTIONS
    }
}

#
# Check if $PROJECTDIR exists
#
if (-not (Test-Path $PROJECTDIR)) {
	GoodBye " Project Directory '$PROJECTDIR' does not exist. Edit PROJECTDIR variable in ps1\env.ps1." $LASTEXITCODE
}

#
# Check if $env:JAVA_HOME is both set and assigned to a valid Path
#
if ($env:JAVA_HOME -eq $null) {
    GoodBye "env:JAVA_HOME Environment Variable is not set. Set the env:JAVA_HOME variable to a vaild JDK runtime location in your Powershell environment or uncomment and edit the 'set-Variable' statement at the beginning of the ps1\env.ps1 file." $LASTEXITCODE 
} elseif (-not (Test-Path $env:JAVA_HOME)) {
	GoodBye "Path for Java Home 'env:JAVA_HOME' does not exist. Set the env:JAVA_HOME variable to a vaild JDK runtime location in your Powershell environment or uncomment and edit the 'set-Variable' statement at the beginning of the ps1\env.ps1 file." $LASTEXITCODE 
}

cd $PROJECTDIR