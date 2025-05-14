import os
import platform


def main():
    libs = os.path.join("build", "libs")
    control = os.path.join("control", libs, "control.jar")
    pattern = "java -cp \"{}\" traintickets.control.Main\n"
    if platform.system() == "Windows":
        separator = ";"
        filename = "start.bat"
    else:
        separator = ":"
        filename = "start.sh"
        pattern = "#/bin/bash\n" + pattern
    jars = f"{control}{separator}{separator.join(map(lambda jar : os.path.join(libs, jar), os.listdir(libs)))}"
    with open(filename, "w", encoding="utf-8") as fin:
        fin.write(pattern.format(jars))


if __name__ == "__main__":
    main()