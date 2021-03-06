package com.realeyes.scte35


/**
 * splice_descriptor()
 */
sealed class SpliceDescriptor {

   /**
    * avail_descriptor()
    */
   data class Avail(val tag:Int, val id:Long, val providerAvailIdentifier:Long) : SpliceDescriptor()


   /**
    * DTMF_descriptor()
    */
   data class DTMF(val tag:Int, val id:Long, val preroll:Int, val dtmfCount:Int, val dtmfChar:String) : SpliceDescriptor()


   /**
    * segmentation_descriptor()
    */
   class Segmentation: SpliceDescriptor() {
      var tag : Int? = null
         internal set

      var id : Long? = null
         internal set

      enum class DeviceRestrictions {
         RestrictGroup0,
         RestrictGroup1,
         RestrictGroup2,
         None;

         companion object {
            fun withOrdinal(o:Int) : DeviceRestrictions {
               return when (o) {
                  0x00 -> RestrictGroup0
                  0x01 -> RestrictGroup1
                  0x02 -> RestrictGroup2
                  0x03 -> None
                  else -> throw IllegalArgumentException("No known device restriction $o.")
               }
            }
         }
      }

      class Component {
         var tag : Int? = null
         var ptsOffset: Long? = null
      }

      var eventId : Long? = null
         internal set

      var cancel : Boolean? = null
         internal set


      // Cancel Indicator false
      var program : Boolean? = null
         internal set

      var mode : SpliceMode? = null
         internal set

      var deliveryRestricted : Boolean? = null
         internal set


      // delivery restricted false
      var webDeliveryAllowed : Boolean? = null
         internal set

      var noRegionalBlackout : Boolean? = null
         internal set

      var archiveAllowed : Boolean? = null
         internal set

      var deviceRestrictions : DeviceRestrictions? = null
         internal set


      // program segmentation flag false
      var components: Array<Component>? = null
         internal set


      // segmentation duration flag == true
      var duration : Long? = null
         internal set


      var upidType : Int? = null
         internal set

      var upidLength : Int? = null
         internal set

      var upid : Array<UPID>? = null
         internal set

      var segmentationType : SegmentationType? = null
         internal set

      var segmentNum : Int? = null
         internal set

      var segmentsExpected : Int? = null
         internal set

      // segmentation type id == 0x34 or 0x36
      var subSegmentNum : Int? = null
         internal set

      var subSegmentsExpected : Int? = null
         internal set
   }


   /**
    * time_descriptor()
    */
   data class Time(val tag:Int, val id:Long, val taiSeconds:Long, val taiNanoseconds:Long, val utcOffset:Int) : SpliceDescriptor()


   /**
    * audio_descriptor()
    */
   data class Audio(val tag:Int, val id:Long, val components:Array<Component>): SpliceDescriptor() {
      data class Component (val tag : Int, val isoCode : Int, val mode : BitStreamMode, val numChannels : Int, val fullServiceAudio : Boolean)
   }

}
