package com.realeyes.scte35

/**
 * splice_insert()
 */
class SpliceEventInsert : SpliceEvent() {

   data class Component(val tag:Int, val time:SpliceTime?)

   var immediateSplice: Boolean? = null // ToDo deprecate if mode is working properly

   // Program Splice Mode
   var spliceTime: SpliceTime? = null
      internal set

   // Component or Immediate Splice Mode
   var components: Array<Component>? = null
      internal set

}
