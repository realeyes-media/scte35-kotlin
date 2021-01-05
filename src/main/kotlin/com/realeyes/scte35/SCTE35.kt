package com.realeyes.scte35

/**
 * A class for material general to the spec. Uninstantiable.
 */
sealed class SCTE35 {

   companion object {
      /**
       * The SCTE35 program clock frequency, 90 kHz.
       */
      @JvmField
      val ClockFrequency : Long = 90000L
   }

}

