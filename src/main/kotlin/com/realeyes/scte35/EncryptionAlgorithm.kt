package com.realeyes.scte35

/**
 * SCTE35 encryption algorithms
 */
enum class EncryptionAlgorithm {
   None,
   DES_ECB,
   DES_CBC,
   TripleDES_EDE3_ECB,
   Reserved,
   Private;

   companion object {
      fun withOrdinal(o:Int) : EncryptionAlgorithm {
         return when (o) {
            0 -> None
            1 -> DES_ECB
            2 -> DES_CBC
            3 -> TripleDES_EDE3_ECB
            in 4 .. 31 -> Reserved
            in 32 .. 63 -> Private
            else -> throw IllegalArgumentException("No known encryption scheme $o.")
         }
      }
   }
}
