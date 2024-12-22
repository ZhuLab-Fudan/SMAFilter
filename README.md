# SMAFilter

A Java-based software to filter SMA cases quickly based on EMG reports.

The frontend and the backend of SMAFilter are developed separately. The frontend is based on Vue3, while the backend is based on Electron and Springboot.

## Download & Installation

Theoretically, the software can be compiled and installed on any platform (Windows, macOS, Linux, etc.) since it is based on Electron. However, considering the majority of users are clinicians in hospitals using Windows, we provide a `exe` installation package for Windows users. You can find a Chinese version of installation manual at `documents/`

Of note, when installing SMAFilter, there will be a Java module installed to run the backend. If you mind this, you can re-compile the frontend without using the provided Java module, and use your own Java environment to run the backend.

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE](./LICENSE) file for details.
