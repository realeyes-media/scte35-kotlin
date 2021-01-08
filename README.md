# SCTE-35

SCTE-35 is the standard for time-based event messaging in video streams. It's most commonly used for dynamically signaling when to insert an ad break, but it could also contain information about program boundaries, blackouts, or stream switching.

# RealEyes Media's SCTE-35 parsing library in Kotlin

A [SCTE-35](https://www.scte.org/SCTEDocs/Standards/ANSI_SCTE%2035%202019r1.pdf) (2019r1) binary message decoder in Kotlin. This library is meant for any Android projects that need to ingest SCTE-35 signals during a stream. The SCTE-35 signal can be delivered to the library as a String or Byte Array and will return a simple object representing the info from the signal.

### API

#### Byte Array

```
val data: UByteArray = ...
val decoder = Decoder(data)
val info: SpliceInfoSection = decoder.getSpliceInfoSection()
```

#### String

```
val message: String = ...
val decoder = Decoder.base64Decoder(message)
val info: SpliceInfoSection = decoder.getSpliceInfoSection()
```

>SpliceInfoSection is a POJO representation of the definitions in [SCTE-35 Standards](https://www.scte.org/SCTEDocs/Standards/ANSI_SCTE%2035%202019r1.pdf))<br>
>For definitions and abbreviations reference page 12 of the Standards

**Note** This API is compatible with Java

#### Experimental Unsigned Types

This library makes use of an experimental feature in Kotlin. This will show a warning in Kotlin. The warning can be disabled with an annotation or a compiler flag. [See here](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-experimental-unsigned-types/)


### Gradle:

The library is hosted on bintray and can be accessed via jCenter

```
// top level gradle
repositories {
    jcenter()
}

// app or module gradle
dependencies {
    implementation 'com.realeyes.scte35:scte35:<version>'
}
```

### License

This project is licensed under the MIT License - see LICENSE file for details