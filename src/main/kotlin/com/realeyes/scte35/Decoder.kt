package com.realeyes.scte35

import android.util.Base64
import kotlin.experimental.and

/*
 * ToDo:
 * Multiplatform (incl Base64 companion decoder method)
 * Actually perform CRC32
 * Decryption
 * SpliceDescriptor.Segmentation subsegment variables?
 * SCTE35Exception for semantic errors?
 */

/**
 * A [SCTE35](https://www.scte.org/SCTEDocs/Standards/ANSI_SCTE%2035%202019r1.pdf) (2019r1)
 * binary message decoder in Kotlin.
 *
 * ### Kotlin:
 * ```
 * val data : ByteArray = ...
 * val decoder = Decoder(data)
 * val info : SpliceInfoSection = decoder.getSpliceInfoSection()
 * ```
 *
 * ### Java:
 * ```
 * String message = ...
 * Decoder decoder = Decoder.base64Decoder(message);
 * SpliceInfoSection info = decoder.getSpliceInfoSection();
 * ```
 *
 * @constructor Initializes the decoder with the given data.
 * @author Gregory Kip
 */
class Decoder(private val data:ByteArray) {

   private val i : ByteIterator = data.iterator()
   private var b : Byte = 0 // current byte
   private var p : Int = 0 // number of bits consumed

   companion object {
      @JvmStatic
      fun base64Decoder(message:String) : Decoder {
         val bytes : ByteArray = Base64.decode(message, Base64.DEFAULT)
         return Decoder(bytes)
      }
   }


   /**
    * Parses the decoder's given data and returns the resulting [SpliceInfoSection].
    *
    * @throws IllegalStateException in case of a syntax error in the given binary message.
    * @throws IllegalArgumentException in the unlikely case of a programmer error.
    */
   fun getSpliceInfoSection() : SpliceInfoSection {
      val info = SpliceInfoSection()

      info.tableId = getByteAsInt()
      info.sectionSyntaxIndicator = getBoolean()
      info.privateIndicator = getBoolean()
      skipBits(2)
      info.sectionLength = getBits(12)
      info.protocolVersion = getByteAsInt()
      val encryptedPacket = getBoolean()
      info.encrypted = encryptedPacket
      val algorithm = getBits(6)
      info.encryptionAlgorithm = EncryptionAlgorithm.withOrdinal(algorithm)
      info.ptsTypeAdjustment = getBitsLong(33)
      info.cwIndex = getByteAsInt()
      info.tier = getBits(12)

      // Splice Command
      val spliceCommandLength = getBits(12)
      val spliceCommandType = getByteAsInt()
      info.command = when (spliceCommandType) {
         0x00 -> SpliceCommand.Null
         0x04 -> getSpliceCommandSchedule()
         0x05 -> getSpliceCommandInsert()
         0x06 -> getSpliceCommandTimeSignal()
         0x07 -> SpliceCommand.BandwidthReservation
         0xff -> getSpliceCommandPrivate(spliceCommandLength)
         else -> throw IllegalArgumentException("Unknown splice command $spliceCommandType.")
      }

      // Splice Descriptors
      val descriptorLoopLength = getBytes(2)
      info.descriptors = getSpliceDescriptors(descriptorLoopLength)
      // stuffing
      // if (encrypted) info.ecrc32 = getBytesLong(4) // ToDo
      // ToDo: two bytes too many in Section 14.1 test
      info.crc32 = getCRC32()

      return info
   }


   private fun getSpliceCommandSchedule() : SpliceCommand.Schedule {
      val spliceCount = getByteAsInt()
      val tmp = arrayOfNulls<SpliceEventSchedule?>(spliceCount)

      for (i in 0 until spliceCount) {
         val event = SpliceEventSchedule()
         event.id = getBytesLong(4).toLong()
         val cancel = getBoolean()
         event.cancel = cancel
         skipBits(7)

         if (!cancel) {
            event.outOfNetwork = getBoolean()
            val program = getBoolean()
            event.program = program
            val isBreak : Boolean = getBoolean()
            skipBits(5)

            if (program) {
               event.mode = SpliceMode.Program
               val timeUTCType : UTCType = getBytesLong(4).toLong()
               event.spliceTime = timeUTCType
            }

            else {
               event.mode = SpliceMode.Component
               val componentCount = getByteAsInt()
               val components = arrayOfNulls<SpliceEventSchedule.Component?>(componentCount)

               for (c in 0 until componentCount) {
                  val tag = getByteAsInt()
                  val time = getBytesLong(4).toLong()
                  components[c] = SpliceEventSchedule.Component(tag, time)
               }

               event.components = Array(componentCount) { c -> components[c]!! }
            }

            if (isBreak) {
               event.breakDuration = getBreakDuration()
            }

            event.programId = getBytes(2).toInt()
            event.availNum = getByteAsInt()
            event.availsExpected = getByteAsInt()
         }

         tmp[i] = event
      }

      val events : Array<SpliceEventSchedule> = Array(spliceCount) { e -> tmp[e]!! }

      return SpliceCommand.Schedule(events)
   }


   private fun getSpliceCommandInsert() : SpliceCommand.Insert {
      val event = SpliceEventInsert()

      event.id = getBytesLong(4).toLong()
      val cancel = getBoolean()
      event.cancel = cancel
      skipBits(7)

      if (!cancel) {
         event.outOfNetwork = getBoolean()
         val program = getBoolean()
         event.program = program
         val isBreak = getBoolean()
         val immediate = getBoolean()
         event.immediateSplice = immediate
         skipBits(4)

         event.mode = when {
            program -> SpliceMode.Program
            immediate -> SpliceMode.Immediate
            else -> SpliceMode.Component
         }

         if (program && !immediate) {
            event.spliceTime = getSpliceTime()
         }

         if (!program) {
            val componentCount = getByteAsInt()
            val components = arrayOfNulls<SpliceEventInsert.Component?>(componentCount)

            for (i in 0 until componentCount) {
               val tag : Int = getByteAsInt()
               val time : SpliceTime? = if (!immediate) getSpliceTime() else null
               components[i] = SpliceEventInsert.Component(tag, time)
            }

            event.components = Array(componentCount) { c -> components[c]!! }
         }

         if (isBreak) {
            event.breakDuration = getBreakDuration()
         }

         event.programId = getBytes(2).toInt()
         event.availNum = getByteAsInt()
         event.availsExpected = getByteAsInt()
      }

      return SpliceCommand.Insert(event)
   }


   private fun getSpliceCommandTimeSignal() : SpliceCommand.TimeSignal {
      val timeSignal = getSpliceTime()
      return SpliceCommand.TimeSignal(timeSignal)
   }


   private fun getSpliceCommandPrivate(k:Int) : SpliceCommand.Private {
      if (k <= 0) throw IllegalArgumentException("Requested k=$k bytes, k non-positive.")
      val id = getBytesLong(4).toLong()
      val bytes = ByteArray(k-4) { 0 }
      for (i in bytes.indices) bytes[i] = getByte()
      return SpliceCommand.Private(id, bytes)
   }


   private fun getSpliceDescriptors(k:Int) : Array<SpliceDescriptor> {
      if (k < 0) throw IllegalArgumentException("Requested k=$k < 0 descriptor bytes.")

      val descriptors = ArrayList<SpliceDescriptor>()

      var n: Int = k*8

      while (n > 0) {
         val start = p

         val tag = getByteAsInt()

         val descriptor : SpliceDescriptor? = when (tag) {
            0x00 -> getSpliceDescriptorAvail(tag)
            0x01 -> getSpliceDescriptorDTMF(tag)
            0x02 -> getSpliceDescriptorSegmentation(tag)
            0x03 -> getSpliceDescriptorTime(tag)
            0x04 -> getSpliceDescriptorAudio(tag)
            else -> throw IllegalArgumentException("Unknown splice descriptor tag $tag.")
         }

         val end = p

         n -= (end - start)

         if (n < 0) throw IllegalStateException("Splice descriptor with tag $tag at bit p=$p was ${end-start} bits long, but only ${end-start+n} bits were available.")

         if (descriptor != null) descriptors.add(descriptor)
      }

      return Array(descriptors.size) { i -> descriptors[i] }
   }


   private fun getSpliceDescriptorAvail(tag:Int) : SpliceDescriptor.Avail? {
      val length = getByteAsInt()
      if (length == 0) return null
      val start = p
      val id = getBytesLong(4)
      val providerAvailIdentifier = getBytesLong(4)
      if ((p-start)/8 != length) throw IllegalStateException("Expected to consume $length bytes, actually consumed ${(p-start)/8}.")
      return SpliceDescriptor.Avail(tag, id, providerAvailIdentifier)
   }


   private fun getSpliceDescriptorDTMF(tag:Int) : SpliceDescriptor.DTMF {
      val length = getByteAsInt()
      val start = p
      val id = getBytesLong(4)
      val preroll = getByteAsInt()
      val dtmfCount = getBits(2)
      skipBits(5)
      val dtmfChar = getString(dtmfCount)
      if ((p-start)/8 != length) throw IllegalStateException("Expected to consume $length bytes, actually consumed ${(p-start)/8}.")
      return SpliceDescriptor.DTMF(tag, id, preroll, dtmfCount, dtmfChar)
   }


   private fun getSpliceDescriptorSegmentation(tag:Int) : SpliceDescriptor.Segmentation {
      val splice = SpliceDescriptor.Segmentation()
      splice.tag = tag
      val length = getByteAsInt()
      val start = p
      splice.id = getBytesLong(4)
      splice.eventId = getBytesLong(4)
      val cancel = getBoolean()
      splice.cancel = cancel
      skipBits(7)

      if (!cancel) {
         val program = getBoolean()
         splice.program = program
         splice.mode =
                 when (program) {
                    true -> SpliceMode.Program
                    false -> SpliceMode.Component
                 }
         val durationFlag = getBoolean()
         val restricted = !getBoolean()
         splice.deliveryRestricted = restricted

         if (restricted) {
            splice.webDeliveryAllowed = getBoolean()
            splice.noRegionalBlackout = getBoolean()
            splice.archiveAllowed = getBoolean()
            splice.deviceRestrictions = SpliceDescriptor.Segmentation.DeviceRestrictions.withOrdinal(getBits(2))
         } else {
            skipBits(5)
         }

         if (!program) {
            val componentCount = getByteAsInt()
            val components = arrayOfNulls<SpliceDescriptor.Segmentation.Component?>(componentCount)
            for (i in 0 until componentCount) {
               val component = SpliceDescriptor.Segmentation.Component()
               component.tag = getByteAsInt()
               skipBits(7)
               component.ptsOffset = getBitsLong(33).toLong()
               components[i] = component
            }
            splice.components = Array(componentCount) { c -> components[c]!! }
         }

         if (durationFlag) {
            splice.duration = getBytesLong(5).toLong()
         }

         val upidType = getByteAsInt()
         splice.upidType = upidType
         val upidLength = getByteAsInt()
         splice.upidLength = upidLength
         val upidBytes = getByteArray(upidLength)
         splice.upid = getUpids(upidType, upidBytes)
         val typeId = getByteAsInt()
         splice.segmentationType = SegmentationType.withId(typeId)
         splice.segmentNum = getByteAsInt()
         splice.segmentsExpected = getByteAsInt()

         /* ToDo: what is going on here? Section 14.1 example contradicts?
         if (typeId == 0x34 || typeId == 0x36) {
            splice.subSegmentNum = getByteAsInt()
            splice.subSegmentsExpected = getByteAsInt()
         }
         */

         if ((p-start)/8 != length) throw IllegalStateException("Expected to consume $length bytes, actually consumed ${(p-start)/8}.")
      }

      return splice
   }

   private fun getUpids(type: Int, bytes: ByteArray): Array<UPID> {
      if (type != UPID.Type.MID.ordinal) {
            return Array(1) { UPID(type, bytes) }
      }

      val upids = ArrayList<UPID>()
      var start = 0

      while (start < bytes.size) {
         val type = bytes[start].toInt()
         start += 1
         val length = bytes[start].toInt()
         start += 1
         val upidBytes = bytes.copyOfRange(start, length + start)
         start += length

         upids.add(UPID(type, upidBytes))
      }

      return Array(upids.size) { i -> upids[i] }
   }

   private fun getSpliceDescriptorTime(tag:Int) : SpliceDescriptor.Time {
      val length = getByteAsInt()
      val start = p
      val id = getBytesLong(4)
      val taiSeconds = getBytesLong(6)
      val taiNanoseconds = getBytesLong(4)
      val utcOffset = getBytes(2).toInt()
      if ((p-start)/8 != length) throw IllegalStateException("Expected to consume $length bytes, actually consumed ${(p-start)/8}.")
      return SpliceDescriptor.Time(tag, id, taiSeconds, taiNanoseconds, utcOffset)
   }


   private fun getSpliceDescriptorAudio(tag:Int) : SpliceDescriptor.Audio {
      val length = getByteAsInt()
      val start = p
      val id = getBytesLong(4)
      val audioCount = getBits(4)
      skipBits(4)

      val tmp = arrayOfNulls<SpliceDescriptor.Audio.Component>(audioCount)

      for (i in 0 until audioCount) {
         val componentTag = getByteAsInt()
         val isoCode = getBytes(3)
         val mode = BitStreamMode.withOrdinal(getBits(3))
         val numChannels = getBits(4)
         val fullServiceAudio = getBoolean()
         tmp[i] = SpliceDescriptor.Audio.Component(componentTag, isoCode, mode, numChannels, fullServiceAudio)
      }

      if ((p-start)/8 != length) throw IllegalStateException("Expected to consume $length bytes, actually consumed ${(p-start)/8}.")

      val components = Array(audioCount) { c -> tmp[c]!! }

      return SpliceDescriptor.Audio(tag, id, components)
   }


   private fun getBreakDuration() : BreakDuration {
      val autoReturn = getBoolean()
      skipBits(6)
      val duration = getBitsLong(33).toLong()
      return BreakDuration(autoReturn, duration)
   }


   private fun getSpliceTime() : SpliceTime {
      val spliceTime = SpliceTime()

      if (getBoolean()) {
         skipBits(6)
         spliceTime.ptsTime = getBitsLong(33).toLong()
      } else {
         skipBits(7)
      }

      return spliceTime
   }


   private fun getCRC32() : Long {
      if (p > (data.size-4)*8) throw IllegalStateException("Requested crc32 (32 bits) but p=$p of ${data.size*8} bits have already been consumed.")
      var crc32 : Long = 0
      for (i in 0 until 4) crc32 = crc32.or(getByteAsLong().shl(8*(3-i)))
      return crc32
   }


   private fun getString(k:Int) : String {
      //println("Requested string of length k=$k from p=$p.")
      if (k <= 0) throw IllegalArgumentException("Requested k=$k characters (bytes), k non-positive.")
      if (p.rem(8) != 0) throw IllegalStateException("Requested a string from off a byte boundary.")
      val sb = StringBuilder(k)
      for (i in 1 .. k) sb.append(getByteAsInt().toChar())
      return sb.toString()
   }


   private fun getBoolean() : Boolean {
      //println("Requested Boolean from p=$p.")
      return getBit() == 1
   }

   private fun getByteAsLong() : Long {
      return getByteAsInt().toLong()
   }

   private fun getByteAsInt() : Int {
      return getByte().toInt() and 0xFF
   }

   private fun getByte() : Byte {
      //println("Requested Byte from p=$p.")

      if (p.rem(8) != 0) throw IllegalStateException("Requested byte from off a byte boundary.")
      b = i.next(); p+=8
      return b
   }


   private fun getShort() : Short {
      //println("Requested Short (16) from p=$p.")

      if (p.rem(8) != 0) throw IllegalStateException("Requested Short (16) from off a byte boundary.")

      var r : Int

      b = i.next(); p+=8
      r = (b.toInt() and 0xFF).shl(8)

      b = i.next(); p+=8
      r = r.or((b.toInt() and 0xFF))

      return r.toShort()
   }


   private fun getInt() : Int {
      //println("Requested Int (32) from p=$p.")

      if (p.rem(8) != 0) throw IllegalStateException("Requested Int (32) from off a byte boundary.")

      var r : Int

      b = i.next(); p+=8
      r = (b.toInt() and 0xFF).shl(8)

      b = i.next(); p+=8
      r = r.or((b.toInt() and 0xFF)).shl(8)

      b = i.next(); p+=8
      r = r.or((b.toInt() and 0xFF))

      return r
   }


   private fun getBytes(k:Int) : Int {
      //println("Requested k=$k bytes from p=$p.")

      if (k <= 0 || k > 4) throw IllegalArgumentException("Requested k=$k bytes, k not in 1 .. 4.")

      val o : Int = p.rem(8)
      if (o != 0) throw IllegalStateException("Requested k=$k bytes from off of a byte boundary, offset o=$o.")

      var bits = 0

      for (j in 1..k) {
         b = i.next(); p+=8
         bits = bits.shl(8) or (b.toInt() and 0xFF)
      }

      return bits
   }


   private fun skipBits(k:Int) {
      //println("Skipping k=$k bits from p=$p.")
      for (i in 1..k) getBit()
   }


   private fun skipBytes(k:Int) {
      //println("Skipping k=$k bytes from p=$p.")
      if (p.rem(8) != 0) throw IllegalStateException("Requested to skip k=$k bytes from off of a byte boundary, p=$p.")
      for (i in 1..k) getByte()
   }


   private fun getBytesLong(k:Int) : Long {
      //println("Requested k=$k bytes, as Long, from p=$p.")

      if (k <= 0 || k > 8) throw IllegalArgumentException("Requested k=$k bytes, k not in 1 .. 8.")
      if (p.rem(8) != 0) throw IllegalStateException("Requested k=$k bytes from off of a byte boundary, p=$p.")

      var bits : Long = 0

      for (j in 1..k) {
         b = i.next(); p+=8
         bits = bits.shl(8) or (b.toInt() and 0xFF).toLong()
      }

      return bits
   }


   private fun getByteArray(k:Int) : ByteArray {
      //println("Requested k=$k bytes from p=$p.")

      if (p.rem(8) != 0) throw IllegalStateException("Requested byte array from off of a byte boundary, p=$p.")

      val r = data.size - p/8

      if (k > r) throw IllegalStateException("Requested k=$k bytes, but only $r remain.")

      val bytes = ByteArray(k)

      for (i in 0 until k) bytes[i] = getByte()

      return bytes
   }


   private fun getBits(k:Int) : Int {
      //println("Requested k=$k bits, as Int, from p=$p.")

      if (k <= 0 || k > 32) throw IllegalArgumentException("Requested k=$k not in 0 < k <= 32.")

      var bits : Int = 0

      for (i in 1 .. k) bits = bits.shl(1).or(getBit())

      return bits
   }


   private fun getBitsLong(k:Int) : Long {
      //println("Requested k=$k bits, as Long, from p=$p.")

      if (k <= 0 || k > 64) throw IllegalArgumentException("Requested k=$k not in 0 < k <= 32.")

      var bits : Long = 0L

      for (i in 1 .. k) bits = bits.shl(1).or(getBitLong())

      return bits
   }


   private fun getBit() : Int {
      //println("Requested a bit, as Int, from p=$p.")

      val r = p % 8 // where are we in the current byte

      if (r == 0) b = i.next()

      val u = b.toInt() and 0xFF
      val mask = 1.shl(7-r)
      val bit : Int = if (u.and(mask) > 0) 1 else 0

      p++

      return bit
   }


   private fun getBitLong() : Long {
      //println("Requested a bit, as Long, from p=$p.")

      val r = p % 8 // how many bits of the current byte

      if (r == 0) b = i.next()

      val u = b.toInt() and 0xFF
      val mask = 1.shl(7-r)
      val bit : Long = if (u.and(mask) > 0) 1 else 0

      p++

      return bit
   }

}
