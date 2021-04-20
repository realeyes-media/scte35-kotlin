package com.realeyes.scte35

/**
 * splice_command()
 */
sealed class SpliceCommand {

   /**
    * splice_null()
    */
   object Null : SpliceCommand()


   /**
    * splice_schedule()
    */
   data class Schedule (val events:Array<SpliceEventSchedule>) : SpliceCommand()


   /**
    * splice_insert()
    */
   data class Insert (val event:SpliceEventInsert): SpliceCommand()


   /**
    * time_signal()
    */
   data class TimeSignal (val timeSignal:SpliceTime) : SpliceCommand()


   /**
    * bandwidth_reservation()
    */
   object BandwidthReservation : SpliceCommand()


   /**
    * private_command()
    */
   data class Private (val id:Long, val bytes:ByteArray) : SpliceCommand()

}
