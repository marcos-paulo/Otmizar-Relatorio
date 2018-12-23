;This file will be executed next to the application bundle image
;I.e. current directory will contain folder OtimizarRelatorio with application files
#define ApplicationName 'OtimizarRelatorio'
#define ApplicationVersion GetFileVersion('OtimizarRelatorio.exe') 
[Setup]
AppId={{com.marcos.relatorio}}
AppName={#ApplicationName}
;AppVersion={#ApplicationVersion}
AppVersion={#ApplicationVersion}
AppVerName={#ApplicationName}
AppPublisher={#ApplicationName}
AppComments={#ApplicationName}
AppCopyright=Copyright (C) 2018
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={pf}\{#ApplicationName}
DisableStartupPrompt=Yes
;DisableDirPage=Yes
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName={#ApplicationName}
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename={#ApplicationName}
Compression=lzma
SolidCompression=yes
;PrivilegesRequired=lowest
SetupIconFile={#ApplicationName}\{#ApplicationName}.ico
UninstallDisplayIcon={app}\{#ApplicationName}.ico
UninstallDisplayName={#ApplicationName}
WizardImageStretch=No
WizardSmallImageFile={#ApplicationName}-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=x64


[Languages]
Name: "brazilianportuguese"; MessagesFile: "compiler:Languages\BrazilianPortuguese.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "{#ApplicationName}\{#ApplicationName}.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "{#ApplicationName}\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\{#ApplicationName}"; Filename: "{app}\{#ApplicationName}.exe"; IconFilename: "{app}\{#ApplicationName}.ico"
Name: "{group}\{cm:UninstallProgram,{#ApplicationName}}"; Filename: "{uninstallexe}"; IconFilename: "{app}\{#ApplicationName}.ico"
Name: "{commondesktop}\{#ApplicationName}"; Filename: "{app}\{#ApplicationName}.exe";  IconFilename: "{app}\{#ApplicationName}.ico"; Tasks: desktopicon

[Run]
Filename: "{app}\{#ApplicationName}.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\{#ApplicationName}.exe"; Description: "{cm:LaunchProgram,{#ApplicationName}}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\{#ApplicationName}.exe"; Parameters: "-install -svcName ""{#ApplicationName}"" -svcDesc ""{#ApplicationName}"" -mainExe ""{#ApplicationName}.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\{#ApplicationName}.exe "; Parameters: "-uninstall -svcName {#ApplicationName} -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
