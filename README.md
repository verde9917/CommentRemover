CommentRemover
[![](https://jitpack.io/v/YoshikiHigo/CommentRemover.svg)](https://jitpack.io/#YoshikiHigo/CommentRemover)
==============

A simple to remove code comment, blank lines, and bracket lines, and indent.

Currently, CommentRemover takes the following options.

-i (mandatory): this option is for specifying an input (a file path, a directory path, or a file content).
If a file path is specified, its content is converted.
If a directory path is specified,  all source files having a specified extension (see option "-l") under the directory are converted.
If a specified string is neither a file nor a directory, CommentRemover regard it as a file content.
So, it converts the content directory.

-l (mandatory): this option is for specifying a programming language.
Currently, "c", "csharp", and "java" can be specified.

-o: this option is for specifying an output.
If you specify a directory path with option "-i", this option is mandatory.
If this option is specified, converted results are output to the specified path.
If not, converted result is output to Standard Output.
Also, you can access to the last converted result by referencing a static field "RommentRemover.result" if you use CommentRemover in your Java program.

-v: this option is for verbose output.
