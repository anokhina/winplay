@echo off
rem https://en.wikibooks.org/wiki/Windows_Batch_Scripting
for %%f in (%1) do (
    echo %%~nf
    mkdir "%%~nf"
    mkdir "%%~nf\Book"
    mkdir "%%~nf\Info\Image"
    copy "C:\Users\Default\Pictures\no_icon.png" "%%~nf\Info\Image\Cover.png"
    move "%%f" "%%~nf\Book"
)