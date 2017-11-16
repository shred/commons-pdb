# commons-pdb ![build status](https://shredzone.org/badge/commons-pdb.svg) ![maven central](https://maven-badges.herokuapp.com/maven-central/org.shredzone.commons/commons-pdb/badge.svg)

A Java library for reading PalmOS PDB database files.

## Features

* Lightweight, only requires Java 1.7 or higher, no other dependencies.
* Android compatible, requires API level 19 (KitKat) or higher.
* Available at [Maven Central](http://search.maven.org/#search|ga|1|a%3A%22commons-pdb%22)

This library offers converters for reading the contents of the most common PDB files:

* Address book
* Datebook
* Todo-List
* Memo
* Notepad (except on Android)

Custom converters can be added for other PDB files. Also, all PDB file contents can be read as raw byte arrays.

## Example

This example reads a calendar PDB file, and converts the records to `ScheduleRecord` objects using a `ScheduleConverter`.

```java
try (PdbFile pdb = new PdbFile(new File("calendar.pdb")) {
    PdbDatabase<ScheduleRecord, CategoryAppInfo> database = pdb.readDatabase(new ScheduleConverter());

    System.out.printf("Name: %s\n", database.getName());

    List<Category> cats = database.getAppInfo().getCategories();
    for (int ix = 0; ix < cats.size(); ix++) {
        System.out.printf("Category %d: %s\n", ix, cats.get(ix));
    }

    for (ScheduleRecord entry : database.getRecords()) {
        System.out.println(entry);
    }
}
```

See the [online documentation](https://shredzone.org/maven/commons-pdb/) for API details.

There is also a [pdbconverter tool](http://pdbconverter.shredzone.org/) offering a GUI and a command line, for converting PDB files.

## Contribute

* Fork the [Source code at GitHub](https://github.com/shred/commons-pdb). Feel free to send pull requests.
* Found a bug? [File a bug report](https://github.com/shred/commons-pdb/issues).

## License

_commons-pdb_ is open source software. The source code is distributed under the terms of [GNU Lesser General Public License Version 3](http://www.gnu.org/licenses/lgpl-3.0.html).
