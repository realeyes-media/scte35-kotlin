package com.realeyes.scte35

/**
 * [ATSC A/52](https://www.atsc.org/wp-content/uploads/2015/03/A52-2018.pdf) Table 5.7 Bit Stream Mode
 */
enum class BitStreamMode {
   CompleteMain,
   MusicAndEffects,
   VisuallyImpaired,
   HearingImpaired,
   Dialogue,
   Commentary,
   Emergency,
   VoiceOver,
   Karaoke;

   companion object {
      fun withOrdinal(o:Int) : BitStreamMode {
         return when (o) {
            0 -> CompleteMain
            1 -> MusicAndEffects
            2 -> VisuallyImpaired
            3 -> HearingImpaired
            4 -> Dialogue
            5 -> Commentary
            6 -> Emergency
            7 -> VoiceOver
            else -> throw IllegalArgumentException("Unknown bit stream mode $o.")
         }
      }
   }
}
