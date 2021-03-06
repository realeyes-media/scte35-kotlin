package com.realeyes.scte35

/**
 * Abstract superclass of [SpliceEventInsert] and [SpliceEventSchedule].
 */
abstract class SpliceEvent {
   var id: Long? = null
      internal set

   var cancel: Boolean? = null
      internal set

   var outOfNetwork: Boolean? = null
      internal set

   var program: Boolean? = null
      internal set

   var mode: SpliceMode? = null
      internal set

   var breakDuration: BreakDuration? = null
      internal set

   var programId: Int? = null
      internal set

   var availNum: Int? = null
      internal set

   var availsExpected: Int? = null
      internal set

}
