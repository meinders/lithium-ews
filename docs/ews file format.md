# Schedule file format specification

This document is licensed under the GNU Free Documentation License.

Copyright 2013  Gerrit Meinders (<meinders1337@gmail.com>)

Permission is granted to copy, distribute and/or modify this document
under the terms of the GNU Free Documentation License, Version 1.3
or any later version published by the Free Software Foundation;
with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts.
A copy of the license is included in the section entitled "GNU
Free Documentation License".


## About this document

This document describes (part of) the file format used by [EasyWorship][] to
store schedules.

[easyworship]: http://easyworship.com/  "EasyWorship Church Presentation Software"

Schedule files are written in a binary file format. There is no official
documentation, nor are there any tools provided with EasyWorship to convert
between schedule files and other (preferably open) file formats. This
specification is intended to enable (at least some) interoperability between
EasyWorship and other software, such as tools to import content.

The information in this document was derived by inspecting schedule files with
a hex-editor. No attempts were made to reverse engineer, decompile or
disassemble the software itself.


## Conventions and data types

This section covers some basic conventions about notations and data types used
in this specification.

Number literals prefixed with '0x' are written in hexadecimal. All other numbers
are written in decimal notation.

Integers types are written as 'int' and the number of bits used, followed by
either 'le' for little-endian byte order, or 'be' for big-endian. For example,
'int32le' indicates a 32-bit integer with little-endian byte order.

Floating point types are written as 'float' and the number of bits used.
Byte order is always little-endian. For example, 'float32' for a common
single-precision (32-bit) floating point value.

Strings are written as simply 'string' for fixed-size strings or 'cstring' for
null-terminated strings. The length specified for cstrings *excludes* the
trailing null character.

Timestamps, written as 'datetime', are stored as 64-bit floating point values.
The value specifies the number of days since 1 January 1900. Using this
notation, the time is simply a fraction of a day. Apparently this is how
[Excel has done it for years][exceldate], and I guess it leaked into .NET.

[exceldate]: http://answers.oreilly.com/topic/1694-how-excel-stores-date-and-time-values/  "How Excel stores date and time values"


# File structure

Schedule files consist of a header, a list of schedule entries and the content
referenced by the schedule entries. The sections below describe each of these
blocks in detail.

    +--------+----------------+-- - -    - - --+---------+-- - -    - - --+
    | Header | Schedule entry | (more entries) | Content | (more content) |
    +--------+----------------+-- - -    - - --+---------+-- - -    - - --+

Note that the meaning of much of the file format remains unknown, because that
content was constant in all examined files. The contents of the unknown fields
can simply be copied from an arbitrary schedule file.


## Header

The header specifies the file format and version, as well as the layout of the
following schedule entries. The remaining content in the header is unknown.

    Offset  Field              Data type    Length    Details
    --------------------------------------------------------------------------------------------------

         0  Filetype           string           38    Specifies the file type and version.
                                                      "EasyWorship Schedule File Version    5"

        56  Entry count        int32le           4    Number of items in the schedule
        60  Entry length       int16le           2    Length of playlist entries: 0x0718 = 1816

The version 3 header is slightly shorter:

    Offset  Field              Data type    Length    Details
    --------------------------------------------------------------------------------------------------

         0  Filetype           string           38    Specifies the file type and version.
                                                      "EasyWorship Schedule File Version    3"

        48  Entry count        int32le           4    Number of items in the schedule
        52  Entry length       int16le           2    Length of playlist entries: 0x05f8 = 1528


## Schedule entry

A schedule entry specifies the title of an item and the location of its content.
There's also a timestamp, which appears to be the modification time of the
content.

Offsets are relative to the start of the schedule entry. The first entry starts
directly after the header.

Any gaps between fields are filled with null characters.

    Offset  Field                  Data type    Length    Details
    ------------------------------------------------------------------------------------------------

         0  Title                  cstring          50
        51  Media resource         cstring         255    Filename or URL
       307  Author                 cstring          50    For scripture: reference with Bible version
                                                          between parentheses. E.g. "John 3:16 (KJV)"
       358  Copyright              cstring         100    For scripture: Bible version. E.g. "KJV"
       459  Administrator          cstring          50
       510  (unknown)                               26
       536  Background             cstring         255    Filename of background image.
                                                          Empty for automatic background.
       792  Timestamp              datetime          8    For songs: content creation/modification time
       800  Content pointer        int32le           4    Position of the content for this entry.
                                                          See 'Text content' and 'Binary content' sections for details.
       820  Content type           int32le           4    0x01 = Song
                                                          0x02 = Scripture
                                                          0x03 = Presentation
                                                          0x04 = Video
                                                          0x05 = Live video
                                                          0x07 = Image
                                                          0x08 = Audio
                                                          0x09 = Web
       824  (unknown)                                4    Checksum, maybe?
                                                          (Why all this checksum anyway? Let the transport layer deal with that.)
       828  (unknown)              int32le           4    Unknown magic value; occurs at offset 28 in presentation stream (see below)
       836  (unknown)              int32le           4    1 for presentations, 0 otherwise.
       840  Presentation length    int32le           4

      1155  Notes                  cstring         160
      1316  (unknown)                               94
      1410  Song number            cstring          10
      1421  (unknown)                               99
      1520  Aspect ratio           int32le           4    0x00 = Automatic
                                                          0x01 = Maintain
                                                          0x02 = Stretch
                                                          0x03 = Zoom
      1524  (unknown)                              292


## Text content

Text content is compressed using the [deflate][] compression algorithm. Song
lyrics are stored in [Rich Text Format][rtf]. Characters appear to be encoded as
bytes using system default character set, in my case 'windows-1252'.

The compressed content is followed by an [Adler-32][] checksum.

    Offset  Field              Data type    Length    Details
    ------------------------------------------------------------------------------------------------
         0  Length             int32le           4    Length (L) of the content, including the compressed content
                                                      and the following fields (14 bytes total).
         4  Content            string         L-14    Content compressed with deflate.
            Checksum           int32be           4    Alder-32 checksum.
            (unknown)                            4    0x51 0x4b 0x03 0x04
            Content length     int32le           4    Length of content after decompression
            (unknown)                            2    0x08 0x00

[deflate]:  http://en.wikipedia.org/wiki/DEFLATE           "DEFLATE"
[rtf]:      http://en.wikipedia.org/wiki/Rich_Text_Format  "Rich Text Format"
[adler-32]: http://en.wikipedia.org/wiki/Adler-32          "Adler-32"


## Binary content

Images and videos are not compressed. As a result the structure that stores them
is pretty straightforward.

    Offset  Field              Data type    Length    Details
    ------------------------------------------------------------------------------------------------
         0  Length             int32le           4    Length (L) of the content.
         4  Content            string            L


## Presentation

In addition to the presentation itself, which is stored as binary contents
(see above), the schedule file also contains preview images of each slide.

    Offset  Field              Data type    Length    Details
    ------------------------------------------------------------------------------------------------
         0  Length             int32le           4    Length of this header
         4  Identifier         string           13    "$ezwppstream$"
                                                      EasyWorship PowerPoint stream? Trendy.
        20  (unknown)                            1    0x02
        28  (unknown)                            4    Same value as schedule entry at offset 828 and 1480.
                                                      Observed values: 0xea00, 0x9600, 0x11c00
        44  Number of slides   int32le           4    Number of slides in the presentation.
        48  Number of slides   int32le           4    (Again, but this time as part of the following list, I assume.)
        52  Slides                                    List of slides (see below).
            Slide content                             Slide content for each slide (see below).


### Slide

Each slide is represented as follows.

         0  Content pointer    int32le           4    Position of slide content (see below).
         8  (unknown)                           12


### Slide content

The actual content of the slide

    Offset  Field              Data type    Length    Details
    ------------------------------------------------------------------------------------------------
         0  (unknown)                            1    0x01
         1  Length             int32le           4    Length (L) of the following uncompressed content.
            Content                              L    Slide as a JPEG image.
