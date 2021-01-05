package com.realeyes.scte35

import kotlin.test.Test

import java.util.Base64

class Section14Test {

   companion object {
      @JvmStatic
      fun decodeBase64(message:String) : UByteArray? {
         return Base64.getDecoder().decode(message).asUByteArray()
      }
   }


   /**
    * SCTE35 2019r1 Section 14.1 Time_Signal -- Placement Opportunity Start
    * 2018-07-16 00:04:57 M274P29528596539
    * Hex=0xFC3034000000000000FFFFF00506FE72BD0050001E021C435545494800008E7FCF0001A599B00808000000002CA0A18A3402009AC9D17E
    * Base64=/DA0AAAAAAAA///wBQb+cr0AUAAeAhxDVUVJSAAAjn/PAAGlmbAICAAAAAAsoKGKNAIAmsnRfg==
    */
   @Test fun testSection_14_1_TimeSignal_PlacementOpportunityStart() {
      // Base64=/DA0AAAAAAAA///wBQb+cr0AUAAeAhxDVUVJSAAAjn/PAAGlmbAICAAAAAAsoKGKNAIAmsnRfg==
      val message = "/DA0AAAAAAAA///wBQb+cr0AUAAeAhxDVUVJSAAAjn/PAAGlmbAICAAAAAAsoKGKNAIAmsnRfg=="
      val data : UByteArray = decodeBase64(message)!!
      val decoder = Decoder(data)
      val info = decoder.getSpliceInfoSection()

      // Decoded length = 55
      // Table ID = 0xFC
      assert(info.tableId == 0xFC)

      // MPEG Short Section
      // Not Private
      assert(!info.privateIndicator!!)

      // Reserved = 0x3
      // Section Length = 52
      assert(info.sectionLength == 52)

      // Protocol Version = 0
      assert(info.protocolVersion == 0)

      // unencrypted Packet
      assert(!info.encrypted!!)

      // PTS Adjustment = 0x000000000
      assert(info.ptsTypeAdjustment == 0L)

      // Tier = 0xfff
      assert(info.tier == 0xff99)

      // Splice Command Length = 0x5
      when (val command = info.command) {
         // Time Signal
         is SpliceCommand.TimeSignal -> {
            // Time = 0x072bd0050 - 21388.766756
            assert(command.timeSignal.ptsTime == 0x072bd0050L)
         }

         else -> assert(false)
      }

      // Descriptor Loop Length = 30
      // not captured

      when (val splice = info.descriptors!![0]) {
         is SpliceDescriptor.Segmentation -> {
            // Segmentation Descriptor - Length=28
            // Not captured

            // Segmentation Event ID = 0x4800008e
            assert(splice.eventId == 0x4800008eL)

            // Segmentation Event Cancel Indicator NOT set
            assert(!splice.cancel!!)

            // Delivery Not Restricted flag = 0
            assert(splice.deliveryRestricted == true)

            // Web Delivery Allowed flag = 0
            assert(!splice.webDeliveryAllowed!!)

            // No Regional Blackout flag = 1
            assert(splice.noRegionalBlackout!!)

            // Archive Allowed flag = 1
            assert(splice.archiveAllowed!!)

            // Device Restrictions = 3
            assert(splice.deviceRestrictions == SpliceDescriptor.Segmentation.DeviceRestrictions.None)

            // Program Segmentation flag SET
            assert(splice.program!!)
            assert(splice.mode == SpliceMode.Program)

            // Segmentation Duration = 0x0001a599b0 = 307.000000 seconds
            assert(splice.duration == 0x0001a599b0L)

            // UPID Type = Turner Identifier length 8
            // Turner Identifier = 0x000000002ca0a18a
            assert(splice.upidType == UPID.Type.TI.ordinal)
            assert(splice.upidLength == 8)
            assert(splice.upid!![0].bytes[0].toInt()==0x00)
            assert(splice.upid!![0].bytes[1].toInt()==0x00)
            assert(splice.upid!![0].bytes[2].toInt()==0x00)
            assert(splice.upid!![0].bytes[3].toInt()==0x00)
            assert(splice.upid!![0].bytes[4].toInt()==0x2c)
            assert(splice.upid!![0].bytes[5].toInt()==0xa0)
            assert(splice.upid!![0].bytes[6].toInt()==0xa1)
            assert(splice.upid!![0].bytes[7].toInt()==0x8a)

            val bytes = UByteArray(8)
            bytes[0]=0x00u
            bytes[1]=0x00u
            bytes[2]=0x00u
            bytes[3]=0x00u
            bytes[4]=0x2cu
            bytes[5]=0xa0u
            bytes[6]=0xa1u
            bytes[7]=0x8au
            val upid = UPID(UPID.Type.TI.ordinal, bytes)
            assert(splice.upid!![0] == upid)

            // Type = Placement Opportunity Start
            assert(splice.segmentationType == SegmentationType.ProviderPlacementOpportunityStart)

            // Segment num = 2
            assert(splice.segmentNum == 2)

            // Segments Expected = 0
            assert(splice.segmentsExpected == 0)
         }

         else -> assert(false)
      }

      // crc32 = 0x9ac9d17e
      assert(info.crc32 == 0x9ac9d17eL)
   }



   /**
    * SCTE35 2019r1 Section 14.2 Splice_Insert
    * 2018-07-16 00:06:59 M274P29540838841
    * Hex=0xFC302F000000000000FFFFF014054800008F7FEFFE7369C02EFE0052CCF500000000000A0008435545490000013562DBA30A
    * Base64=/DAvAAAAAAAA///wFAVIAACPf+/+c2nALv4AUsz1AAAAAAAKAAhDVUVJAAABNWLbowo=
    */
   @Test fun testSection_14_2_SpliceInsert() {
      val message = "/DAvAAAAAAAA///wFAVIAACPf+/+c2nALv4AUsz1AAAAAAAKAAhDVUVJAAABNWLbowo="
      val data = decodeBase64(message)!!
      val decoder = Decoder(data)
      val info = decoder.getSpliceInfoSection()

      // Decoded length = 50
      // Table ID = 0xFC
      assert(info.tableId == 0xfc)

      // MPEG Short Section
      // Not Private
      assert(!info.privateIndicator!!)

      // Reserved = 0x3
      // Section Length = 47
      assert(info.sectionLength == 47)

      // Protocol Version = 0
      assert(info.protocolVersion == 0)

      // unencrypted Packet
      assert(!info.encrypted!!)

      // PTS Adjustment = 0x000000000
      assert(info.ptsTypeAdjustment == 0L)

      // Tier = 0xfff
      assert(info.tier == 0xfff)

      // Splice Command Length = 0x14
      // N/A

      // Splice Insert
      when (val command = info.command) {
         is SpliceCommand.Insert -> {
            // Splice Event ID = 0x4800008f
            assert(command.event.id == 0x4800008fL)

            // Flags OON=1 Prog=1 Duration=1 Immediate=0
            assert(command.event.outOfNetwork!!)
            assert(command.event.program!!)
            assert(command.event.breakDuration != null)
            assert(!command.event.immediateSplice!!)
            assert(command.event.mode == SpliceMode.Program)

            // Splice timeType = 0x07369c02e - 21514.559089
            assert(command.event.spliceTime!!.ptsTime == 0x07369c02eL)

            // Auto Return
            assert(command.event.breakDuration!!.autoReturn)

            // break duration = 0x00052ccf5 = 60.293567 seconds
            assert(command.event.breakDuration!!.duration == 0x00052ccf5L)

            // Unique Program ID = 0
            assert(command.event.programId!! == 0)

            // Avail Num = 0
            assert(command.event.availNum!! == 0)

            // Avails Expected = 0
            assert(command.event.availsExpected == 0)
         }

         else -> assert(false)
      }

      // Descriptor Loop Length = 10
      // not captured

      when (val avail = info.descriptors!![0]) {
         is SpliceDescriptor.Avail -> {
            // Avail Descriptor - Length=8
            // Avail Descriptor = 0x00000135 - 309
            assert(avail.providerAvailIdentifier == 0x00000135L)
         }

         else -> assert(false)
      }

      // crc32 = 0x62dba30a
      assert(info.crc32 == 0x62dba30aL)
    }



   /**
    * SCTE35 2019r1 Section 14.3.Time_Signal – Placement Opportunity End
    * 2018-07-16 00:10:04 M274P29559224252
    * Hex = 0xFC302F000000000000FFFFF00506FE746290A000190217435545494800008E7F9F0808000000002CA0A18A350200A9CC6758
    * Base64=/DAvAAAAAAAA///wBQb+dGKQoAAZAhdDVUVJSAAAjn+fCAgAAAAALKChijUCAKnMZ1g=
    */
   @Test fun testSection_14_3_TimeSignal_PlacementOpportunityEnd() {
      val message = "/DAvAAAAAAAA///wBQb+dGKQoAAZAhdDVUVJSAAAjn+fCAgAAAAALKChijUCAKnMZ1g="
      val data = decodeBase64(message)!!
      val decoder = Decoder(data)
      val info = decoder.getSpliceInfoSection()

      // Decoded length = 50
      // Table ID = 0xFC
      assert(info.tableId == 0xfc)

      // MPEG Short Section
      // Not Private
      assert(!info.privateIndicator!!)

      // Reserved = 0x3
      // Section Length = 47
      assert(info.sectionLength == 47)

      // Protocol Version = 0
      assert(info.protocolVersion == 0)

      // unencrypted Packet
      assert(!info.encrypted!!)

      // PTS Adjustment = 0x000000000
      assert(info.ptsTypeAdjustment == 0L)

      // Tier = 0xfff
      assert(info.tier == 0xfff)

      // Splice Command Length = 0x5
      // N/A

      when (val command = info.command) {
         // Time Signal
         is SpliceCommand.TimeSignal -> {
            // Time = 0x0746290a0 - 21695.740089
            assert(command.timeSignal.ptsTime == 0x0746290a0L)
         }

         else -> assert(false)
      }

      // Descriptor Loop Length = 25
      // not captured

      when (val splice = info.descriptors!![0]) {
         is SpliceDescriptor.Segmentation -> {
            // Segmentation Descriptor -Length = 23
            // Not captured

            // Segmentation Event ID = 0x4800008e
            assert(splice.eventId == 0x4800008eL)

            // Segmentation Event Cancel Indicator NOT set
            assert(!splice.cancel!!)

            // Delivery Not Restricted flag = 0
            assert(splice.deliveryRestricted!!)

            // Web Delivery Allowed flag = 1
            assert(splice.webDeliveryAllowed!!)

            // No Regional Blackout flag = 1
            assert(splice.noRegionalBlackout!!)

            // Archive Allowed flag = 1
            assert(splice.archiveAllowed!!)

            // Device Restrictions = 3
            assert(splice.deviceRestrictions == SpliceDescriptor.Segmentation.DeviceRestrictions.None)

            // Program Segmentation flag SET
            assert(splice.program!!)
            assert(splice.mode == SpliceMode.Program)

            // UPID Type = Turner Identifier length 8
            // Turner Identifier = 0x000000002ca0a18a
            assert(splice.upidType == 8)
            assert(splice.upidLength == 8)
            val bytes = UByteArray(8)
            bytes[0]=0x00u
            bytes[1]=0x00u
            bytes[2]=0x00u
            bytes[3]=0x00u
            bytes[4]=0x2cu
            bytes[5]=0xa0u
            bytes[6]=0xa1u
            bytes[7]=0x8au
            val upid = UPID(UPID.Type.TI.ordinal, bytes)
            assert(splice.upid!![0] == upid)

            // Type = Placement Opportunity End
            assert(splice.segmentationType == SegmentationType.ProviderPlacementOpportunityEnd)

            // Segment num = 2
            assert(splice.segmentNum == 2)

            // Segments Expected = 0
            assert(splice.segmentsExpected == 0)
         }

         else -> assert(false)
      }

      // crc32 = 0xa9cc6758
      assert(info.crc32 == 0xa9cc6758L)
   }



   /**
    * SCTE35 2019r1 Section 14.4. Time_Signal – Program Start/End
    * 2018-07-16 00:00:15 M274P29500484335
    * Hex=0xFC3048000000000000FFFFF00506FE7A4D88B60032021743554549480000187F9F0808000000002CCBC344110000021743554549480000197F9F0808000000002CA4DBA01000009972E343
    * Base64=/DBIAAAAAAAA///wBQb+ek2ItgAyAhdDVUVJSAAAGH+fCAgAAAAALMvDRBEAAAIXQ1VFSUgAABl/nwgIAAAAACyk26AQAACZcuND
    */
   @Test fun testSection_14_4_TimeSignal_ProgramStartEnd() {
      val message = "/DBIAAAAAAAA///wBQb+ek2ItgAyAhdDVUVJSAAAGH+fCAgAAAAALMvDRBEAAAIXQ1VFSUgAABl/nwgIAAAAACyk26AQAACZcuND"
      val data = decodeBase64(message)!!
      val decoder = Decoder(data)
      val info = decoder.getSpliceInfoSection()

      // Decoded length = 75
      // Table ID = 0xFC
      assert(info.tableId == 0xfc)

      // MPEG Short Section
      // Not Private
      assert(!info.privateIndicator!!)

      // Reserved = 0x3
      // Section Length = 72
      assert(info.sectionLength == 72)

      // Protocol Version = 0
      assert(info.protocolVersion == 0)

      // unencrypted Packet
      assert(!info.encrypted!!)

      // PTS Adjustment = 0x000000000
      assert(info.ptsTypeAdjustment == 0L)

      // Tier = 0xfff
      assert(info.tier == 0xfff)

      // Splice Command Length = 0x5
      // N/A

      when (val command = info.command) {
         // Time Signal
         is SpliceCommand.TimeSignal -> {
            // Time = 0x07a4d88b6 - 22798.906911
            assert(command.timeSignal.ptsTime == 0x07a4d88b6L)
         }

         else -> assert(false)
      }

      // Descriptor Loop Length = 50
      // not captured

      when (val splice = info.descriptors!![0]) {
         is SpliceDescriptor.Segmentation -> {
            // Segmentation Descriptor - Length=23
            // Not captured

            // Segmentation Event ID = 0x48000018
            assert(splice.eventId == 0x48000018L)

            // Segmentation Event Cancel Indicator NOT set
            assert(!splice.cancel!!)

            // Delivery Not Restricted flag = 0
            assert(splice.deliveryRestricted!!)

            // Web Delivery Allowed flag = 1
            assert(splice.webDeliveryAllowed!!)

            // No Regional Blackout flag = 1
            assert(splice.noRegionalBlackout!!)

            // Archive Allowed flag = 1
            assert(splice.archiveAllowed!!)

            // Device Restrictions = 3
            assert(splice.deviceRestrictions == SpliceDescriptor.Segmentation.DeviceRestrictions.None)

            // Program Segmentation flag SET
            assert(splice.program!!)
            assert(splice.mode == SpliceMode.Program)

            // UPID Type = Turner Identifier length = 8
            // Turner Identifier = 0x00000002ccbc344
            assert(splice.upidType == 8)
            assert(splice.upidLength == 8)
            val bytes = UByteArray(8)
            bytes[0] = 0x00u
            bytes[1] = 0x00u
            bytes[2] = 0x00u
            bytes[3] = 0x00u
            bytes[4] = 0x2cu
            bytes[5] = 0xcbu
            bytes[6] = 0xc3u
            bytes[7] = 0x44u
            val upid = UPID(UPID.Type.TI.ordinal, bytes)
            assert(splice.upid!![0] == upid)

            // Type = Program End
            assert(splice.segmentationType == SegmentationType.ProgramEnd)

            // Segment num = 0
            assert(splice.segmentNum == 0)

            // Segments Expected = 0
            assert(splice.segmentsExpected == 0)
         }

         else -> assert(false)
      }


      when (val splice = info.descriptors!![1]) {
         is SpliceDescriptor.Segmentation -> {
            // Segmentation Descriptor - Length=23
            // Not captured

            // Segmentation Event ID = 0x48000019
            assert(splice.eventId == 0x48000019L)

            // Segmentation Event Cancel Indicator NOT set
            assert(!splice.cancel!!)

            // Delivery Not Restricted flag = 0
            assert(splice.deliveryRestricted!!)

            // Web Delivery Allowed flag = 1
            assert(splice.webDeliveryAllowed!!)

            // No Regional Blackout flag = 1
            assert(splice.noRegionalBlackout!!)

            // Archive Allowed flag = 1
            assert(splice.archiveAllowed!!)

            // Device Restrictions = 3
            assert(splice.deviceRestrictions == SpliceDescriptor.Segmentation.DeviceRestrictions.None)

            // Program Segmentation flag SET
            assert(splice.program!!)
            assert(splice.mode == SpliceMode.Program)

            // UPID Type = Turner Identifier length = 8
            // Turner Identifier = 0x000000002ca4dba0
            assert(splice.upidType == 8)
            assert(splice.upidLength == 8)
            val bytes = UByteArray(8)
            bytes[0] = 0x00u
            bytes[1] = 0x00u
            bytes[2] = 0x00u
            bytes[3] = 0x00u
            bytes[4] = 0x2cu
            bytes[5] = 0xa4u
            bytes[6] = 0xdbu
            bytes[7] = 0xa0u
            val upid = UPID(UPID.Type.TI.ordinal, bytes)
            assert(splice.upid!![0] == upid)

            // Type = Program Start
            assert(splice.segmentationType == SegmentationType.ProgramStart)

            // Segment num = 0
            assert(splice.segmentNum == 0)

            // Segments Expected = 0
            assert(splice.segmentsExpected == 0)
         }

         else -> assert(false)
      }

      // crc32 = 0x9972e343
      assert(info.crc32 == 0x9972e343L)
   }



   /**
    * SCTE35 2019r1 Section 14.5 Time_Signal – Program Overlap Start
    * 2018-07-16 02:59:52 M274P30575324060
    * Hex=0xFC302F000000000000FFFFF00506FEAEBFFF640019021743554549480000087F9F0808000000002CA56CF5170000951DB0A8
    * Base64=/DAvAAAAAAAA///wBQb+rr//ZAAZAhdDVUVJSAAACH+fCAgAAAAALKVs9RcAAJUdsKg=
    */
   @Test fun testSection_14_5_TimeSignal_ProgramOverlapStart() {
      val message = "/DAvAAAAAAAA///wBQb+rr//ZAAZAhdDVUVJSAAACH+fCAgAAAAALKVs9RcAAJUdsKg="
      val data = decodeBase64(message)!!
      val decoder = Decoder(data)
      val info = decoder.getSpliceInfoSection()

      // Decoded length = 50
      // Table ID = 0xFC
      assert(info.tableId == 0xfc)

      // MPEG Short Section

      // Not Private
      assert(!info.privateIndicator!!)

      // Reserved = 0x3

      // Section Length = 47
      assert(info.sectionLength == 47)

      // Protocol Version = 0
      assert(info.protocolVersion == 0)

      // unencrypted Packet
      assert(!info.encrypted!!)

      // PTS Adjustment = 0x000000000
      assert(info.ptsTypeAdjustment == 0L)

      // Tier = 0xfff
      assert(info.tier == 0xfff)

      // Splice Command Length = 0x5
      // N/A

      when (val command = info.command) {
         // Time Signal
         is SpliceCommand.TimeSignal -> {
            // Time = 0x0aebfff64 - 32575.759333
            assert(command.timeSignal.ptsTime == 0x0aebfff64L)
         }

         else -> assert(false)
      }


      // Descriptor Loop Length = 25
      // not captured

      when (val splice = info.descriptors!![0]) {
         is SpliceDescriptor.Segmentation -> {
            // Segmentation Descriptor - Length=23
            // Not captured

            // Segmentation Event ID = 0x48000008
            assert(splice.eventId == 0x48000008L)

            // Segmentation Event Cancel Indicator NOT set
            assert(!splice.cancel!!)

            // Delivery Not Restricted flag = 0
            assert(splice.deliveryRestricted!!)

            // Web Delivery Allowed flag = 1
            assert(splice.webDeliveryAllowed!!)

            // No Regional Blackout flag = 1
            assert(splice.noRegionalBlackout!!)

            // Archive Allowed flag = 1
            assert(splice.archiveAllowed!!)

            // Device Restrictions = 3
            assert(splice.deviceRestrictions == SpliceDescriptor.Segmentation.DeviceRestrictions.None)

            // Program Segmentation flag SET
            assert(splice.program!!)
            assert(splice.mode == SpliceMode.Program)

            // UPID Type = Turner Identifier length = 8
            // Turner Identifier = 0x000000002ca56cf5
            assert(splice.upidType == 8)
            assert(splice.upidLength == 8)
            val bytes = UByteArray(8)
            bytes[0] = 0x00u
            bytes[1] = 0x00u
            bytes[2] = 0x00u
            bytes[3] = 0x00u
            bytes[4] = 0x2cu
            bytes[5] = 0xa5u
            bytes[6] = 0x6cu
            bytes[7] = 0xf5u
            val upid = UPID(UPID.Type.TI.ordinal, bytes)
            assert(splice.upid!![0] == upid)

            // Type = Program Overlap Start
            assert(splice.segmentationType == SegmentationType.ProgramOverlapStart)

            // Segment num = 0
            assert(splice.segmentNum == 0)

            // Segments Expected = 0
            assert(splice.segmentsExpected == 0)
         }

         else -> assert(false)
      }

      // crc32 = 0x951db0a8
      assert(info.crc32 == 0x951db0a8L)
   }



   /**
    * SCTE35 2019r1 Section 14.6 Time_Signal – Program Blackout Override / Program End
    * Since the restriction flags are not evaluated on an End message, the use of the program blackout override can be used in the case of an overlap start or other condition where the restrictions may need to be changed during a program playback.
    * 2018-07-16 01:45:45 M274P30131806863
    * Hex=0xFC3048000000000000FFFFF00506FE932E380B00320217435545494800000A7F9F0808000000002CA0A1E3180000021743554549480000097F9F0808000000002CA0A18A110000B4217EB0
    * Base64=/DBIAAAAAAAA///wBQb+ky44CwAyAhdDVUVJSAAACn+fCAgAAAAALKCh4xgAAAIXQ1VFSUgAAAl/nwgIAAAAACygoYoRAAC0IX6w
    */
   @Test fun testSection_14_6_TimeSignal_ProgramBlackoutOverride_ProgramEnd() {
      val message = "/DBIAAAAAAAA///wBQb+ky44CwAyAhdDVUVJSAAACn+fCAgAAAAALKCh4xgAAAIXQ1VFSUgAAAl/nwgIAAAAACygoYoRAAC0IX6w"
      val data = decodeBase64(message)!!
      val decoder = Decoder(data)
      val info = decoder.getSpliceInfoSection()

      // Decoded length = 75
      // Table ID = 0xFC
      assert(info.tableId == 0xfc)

      // MPEG Short Section
      // Not Private
      assert(!info.privateIndicator!!)

      // Reserved = 0x3
      // Section Length = 72
      assert(info.sectionLength == 72)

      // Protocol Version = 0
      assert(info.protocolVersion == 0)

      // unencrypted Packet
      assert(!info.encrypted!!)

      // PTS Adjustment = 0x000000000
      assert(info.ptsTypeAdjustment == 0L)

      // Tier = 0xfff
      assert(info.tier == 0xfff)

      // Splice Command Length = 0x5
      // N/A

      when (val command = info.command) {
         // Time Signal
         is SpliceCommand.TimeSignal -> {
            // Time = 0x0932e380b - 27436.441722
            assert(command.timeSignal.ptsTime == 0x0932e380bL)
         }

         else -> assert(false)
      }

      // Descriptor Loop Length = 50
      // not captured

      when (val splice = info.descriptors!![0]) {
         is SpliceDescriptor.Segmentation -> {
            // Segmentation Descriptor - Length=23
            // Not captured

            // Segmentation Event ID = 0x4800000a
            assert(splice.eventId == 0x4800000aL)

            // Segmentation Event Cancel Indicator NOT set
            assert(!splice.cancel!!)

            // Delivery Not Restricted flag = 0
            assert(splice.deliveryRestricted!!)

            // Web Delivery Allowed flag = 1
            assert(splice.webDeliveryAllowed!!)

            // No Regional Blackout flag = 1
            assert(splice.noRegionalBlackout!!)

            // Archive Allowed flag = 1
            assert(splice.archiveAllowed!!)

            // Device Restrictions = 3
            assert(splice.deviceRestrictions == SpliceDescriptor.Segmentation.DeviceRestrictions.None)

            // Program Segmentation flag SET
            assert(splice.program!!)
            assert(splice.mode == SpliceMode.Program)

            // UPID Type = Turner Identifier length = 8
            // Turner Identifier = 0x00000000 2c a0 a1 e3
            assert(splice.upidType == 8)
            assert(splice.upidLength == 8)
            val bytes = UByteArray(8)
            bytes[0] = 0x00u
            bytes[1] = 0x00u
            bytes[2] = 0x00u
            bytes[3] = 0x00u
            bytes[4] = 0x2cu
            bytes[5] = 0xa0u
            bytes[6] = 0xa1u
            bytes[7] = 0xe3u
            val upid = UPID(UPID.Type.TI.ordinal, bytes)
            assert(splice.upid!![0] == upid)

            // Type = Program Blackout Override
            assert(splice.segmentationType == SegmentationType.ProgramBlackoutOverride)

            // Segment num = 0
            assert(splice.segmentNum == 0)

            // Segments Expected = 0
            assert(splice.segmentsExpected == 0)
         }

         else -> assert(false)
      }


      when (val splice = info.descriptors!![1]) {
         is SpliceDescriptor.Segmentation -> {
            // Segmentation Descriptor - Length=23
            // Not captured

            // Segmentation Event ID = 0x48000009
            assert(splice.eventId == 0x48000009L)

            // Segmentation Event Cancel Indicator NOT set
            assert(!splice.cancel!!)

            // Delivery Not Restricted flag = 0
            assert(splice.deliveryRestricted!!)

            // Web Delivery Allowed flag = 1
            assert(splice.webDeliveryAllowed!!)

            // No Regional Blackout flag = 1
            assert(splice.noRegionalBlackout!!)

            // Archive Allowed flag = 1
            assert(splice.archiveAllowed!!)

            // Device Restrictions = 3
            assert(splice.deviceRestrictions == SpliceDescriptor.Segmentation.DeviceRestrictions.None)

            // Program Segmentation flag SET
            assert(splice.program!!)
            assert(splice.mode == SpliceMode.Program)

            // UPID Type = Turner Identifier length = 8
            // Turner Identifier = 0x00000000 2c a0 a1 8a
            assert(splice.upidType == 8)
            assert(splice.upidLength == 8)
            val bytes = UByteArray(8)
            bytes[0] = 0x00u
            bytes[1] = 0x00u
            bytes[2] = 0x00u
            bytes[3] = 0x00u
            bytes[4] = 0x2cu
            bytes[5] = 0xa0u
            bytes[6] = 0xa1u
            bytes[7] = 0x8au
            val upid = UPID(UPID.Type.TI.ordinal, bytes)
            assert(splice.upid!![0] == upid)

            // Type = Program End
            assert(splice.segmentationType == SegmentationType.ProgramEnd)

            // Segment num = 0
            assert(splice.segmentNum == 0)

            // Segments Expected = 0
            assert(splice.segmentsExpected == 0)
         }

         else -> assert(false)
      }

      // crc32 = 0xb4217eb0
      assert(info.crc32 == 0xb4217eb0L)
   }



   /**
    * SCTE35 2019r1 Section 14.7 Time_Signal – Program End
    * 2018-07-16 03:00:28 M274P30578915636
    * Hex=0xFC302F000000000000FFFFF00506FEAEF17C4C0019021743554549480000077F9F0808000000002CA56C97110000C4876A2E
    * Base64=/DAvAAAAAAAA///wBQb+rvF8TAAZAhdDVUVJSAAAB3+fCAgAAAAALKVslxEAAMSHai4=
    */
   @Test fun testSection_14_7_TimeSignal_ProgramEnd() {
      val message = "/DAvAAAAAAAA///wBQb+rvF8TAAZAhdDVUVJSAAAB3+fCAgAAAAALKVslxEAAMSHai4="
      val data = decodeBase64(message)!!
      val decoder = Decoder(data)
      val info = decoder.getSpliceInfoSection()

      // Decoded length = 75
      // Table ID = 0xFC
      assert(info.tableId == 0xfc)

      // MPEG Short Section
      // Not Private
      assert(!info.privateIndicator!!)

      // Reserved = 0x3
      // Section Length = 47
      assert(info.sectionLength == 47)

      // Protocol Version = 0
      assert(info.protocolVersion == 0)

      // unencrypted Packet
      assert(!info.encrypted!!)

      // PTS Adjustment = 0x000000000
      assert(info.ptsTypeAdjustment == 0L)

      // Tier = 0xfff
      assert(info.tier == 0xfff)

      // Splice Command Length = 0x5
      // N/A

      when (val command = info.command) {
         // Time Signal
         is SpliceCommand.TimeSignal -> {
            // Time = 0x0aef17c4c - 32611.795333
            assert(command.timeSignal.ptsTime == 0x0aef17c4cL)
         }

         else -> assert(false)
      }

      // Descriptor Loop Length = 25
      // not captured

      when (val splice = info.descriptors!![0]) {
         is SpliceDescriptor.Segmentation -> {
            // Segmentation Descriptor - Length=23
            // Not captured

            // Segmentation Event ID = 0x48000007
            assert(splice.eventId == 0x48000007L)

            // Segmentation Event Cancel Indicator NOT set
            assert(!splice.cancel!!)

            // Delivery Not Restricted flag = 0
            assert(splice.deliveryRestricted!!)

            // Web Delivery Allowed flag = 1
            assert(splice.webDeliveryAllowed!!)

            // No Regional Blackout flag = 1
            assert(splice.noRegionalBlackout!!)

            // Archive Allowed flag = 1
            assert(splice.archiveAllowed!!)

            // Device Restrictions = 3
            assert(splice.deviceRestrictions == SpliceDescriptor.Segmentation.DeviceRestrictions.None)

            // Program Segmentation flag SET
            assert(splice.program!!)
            assert(splice.mode == SpliceMode.Program)

            // UPID Type = Turner Identifier length = 8
            // Turner Identifier = 0x00000000 2c a5 6c 97
            assert(splice.upidType == 8)
            assert(splice.upidLength == 8)
            val bytes = UByteArray(8)
            bytes[0] = 0x00u
            bytes[1] = 0x00u
            bytes[2] = 0x00u
            bytes[3] = 0x00u
            bytes[4] = 0x2cu
            bytes[5] = 0xa5u
            bytes[6] = 0x6cu
            bytes[7] = 0x97u
            val upid = UPID(UPID.Type.TI.ordinal, bytes)
            assert(splice.upid!![0] == upid)

            // Type = Program End
            assert(splice.segmentationType == SegmentationType.ProgramEnd)

            // Segment num = 0
            assert(splice.segmentNum == 0)

            // Segments Expected = 0
            assert(splice.segmentsExpected == 0)
         }

         else -> assert(false)
      }

      // crc32 = 0xc4876a2e
      assert(info.crc32 == 0xc4876a2eL)
   }


   /**
    * SCTE35 2019r1 Section 14.8 Time_Signal – Program Start/End - Placement Opportunity End
    * This is a complex message, although one that can occur frequently as many ad breaks are placed at the end of the program. The implementer should take care though to find the length and current practice is to try and keep the message in a single transport packet.
    * 2018-07-16 03:00:33 M274P30579401569
    * Hex=0xFC3061000000000000FFFFF00506FEA8CD44ED004B021743554549480000AD7F9F0808000000002CB2D79D350200021743554549480000267F9F0808000000002CB2D79D110000021743554549480000277F9F0808000000002CB2D7B31000008A18869F
    * Base64=/DBhAAAAAAAA///wBQb+qM1E7QBLAhdDVUVJSAAArX+fCAgAAAAALLLXnTUCAAIXQ1VFSUgAACZ/nwgIAAAAACyy150RAAACF0NVRUlIAAAnf58 ICAAAAAAsstezEAAAihiGnw==
    */
   @Test fun testSection_14_8_TimeSignal_ProgramStartEnd_PlacementOpportunityEnd() {
      val message = "/DBhAAAAAAAA///wBQb+qM1E7QBLAhdDVUVJSAAArX+fCAgAAAAALLLXnTUCAAIXQ1VFSUgAACZ/nwgIAAAAACyy150RAAACF0NVRUlIAAAnf58ICAAAAAAsstezEAAAihiGnw=="
      val data = decodeBase64(message)!!
      val decoder = Decoder(data)
      val info = decoder.getSpliceInfoSection()

      // Decoded length = 75
      // Table ID = 0xFC
      assert(info.tableId == 0xfc)

      // MPEG Short Section
      // Not Private
      assert(!info.privateIndicator!!)

      // Reserved = 0x3
      // Section Length = 97
      assert(info.sectionLength == 97)

      // Protocol Version = 0
      assert(info.protocolVersion == 0)

      // unencrypted Packet
      assert(!info.encrypted!!)

      // PTS Adjustment = 0x000000000
      assert(info.ptsTypeAdjustment == 0L)

      // Tier = 0xfff
      assert(info.tier == 0xfff)

      // Splice Command Length = 0x5
      // N/A

      when (val command = info.command) {
         // Time Signal
         is SpliceCommand.TimeSignal -> {
            // Time = 0x0a8cd44ed - 31466.942367
            assert(command.timeSignal.ptsTime == 0x0a8cd44edL)
         }

         else -> assert(false)
      }


      // Descriptor Loop Length = 75
      // not captured

      when (val splice = info.descriptors!![0]) {
         is SpliceDescriptor.Segmentation -> {
            // Segmentation Descriptor - Length=23
            // Not captured

            // Segmentation Event ID = 0x480000ad
            assert(splice.eventId == 0x480000adL)

            // Segmentation Event Cancel Indicator NOT set
            assert(!splice.cancel!!)

            // Delivery Not Restricted flag = 0
            assert(splice.deliveryRestricted!!)

            // Web Delivery Allowed flag = 1
            assert(splice.webDeliveryAllowed!!)

            // No Regional Blackout flag = 1
            assert(splice.noRegionalBlackout!!)

            // Archive Allowed flag = 1
            assert(splice.archiveAllowed!!)

            // Device Restrictions = 3
            assert(splice.deviceRestrictions == SpliceDescriptor.Segmentation.DeviceRestrictions.None)

            // Program Segmentation flag SET
            assert(splice.program!!)
            assert(splice.mode == SpliceMode.Program)

            // UPID Type = Turner Identifier length = 8
            // Turner Identifier = 0x000000002c b2 d7 9d
            assert(splice.upidType == 8)
            assert(splice.upidLength == 8)
            val bytes = UByteArray(8)
            bytes[0] = 0x00u
            bytes[1] = 0x00u
            bytes[2] = 0x00u
            bytes[3] = 0x00u
            bytes[4] = 0x2cu
            bytes[5] = 0xb2u
            bytes[6] = 0xd7u
            bytes[7] = 0x9du
            val upid = UPID(UPID.Type.TI.ordinal, bytes)
            assert(splice.upid!![0] == upid)

            // Type = Placement Opportunity End
            assert(splice.segmentationType == SegmentationType.ProviderPlacementOpportunityEnd)

            // Segment num = 2
            assert(splice.segmentNum == 2)

            // Segments Expected = 0
            assert(splice.segmentsExpected == 0)
         }

         else -> assert(false)
      }


      when (val splice = info.descriptors!![1]) {
         is SpliceDescriptor.Segmentation -> {
            // Segmentation Descriptor - Length=23
            // Not captured

            // Segmentation Event ID = 0x48000026
            assert(splice.eventId == 0x48000026L)

            // Segmentation Event Cancel Indicator NOT set
            assert(!splice.cancel!!)

            // Delivery Not Restricted flag = 0
            assert(splice.deliveryRestricted!!)

            // Web Delivery Allowed flag = 1
            assert(splice.webDeliveryAllowed!!)

            // No Regional Blackout flag = 1
            assert(splice.noRegionalBlackout!!)

            // Archive Allowed flag = 1
            assert(splice.archiveAllowed!!)

            // Device Restrictions = 3
            assert(splice.deviceRestrictions == SpliceDescriptor.Segmentation.DeviceRestrictions.None)

            // Program Segmentation flag SET
            assert(splice.program!!)
            assert(splice.mode == SpliceMode.Program)

            // UPID Type = Turner Identifier length = 8
            // Turner Identifier = 0x000000002c b2 d7 9d
            assert(splice.upidType == 8)
            assert(splice.upidLength == 8)
            val bytes = UByteArray(8)
            bytes[0] = 0x00u
            bytes[1] = 0x00u
            bytes[2] = 0x00u
            bytes[3] = 0x00u
            bytes[4] = 0x2cu
            bytes[5] = 0xb2u
            bytes[6] = 0xd7u
            bytes[7] = 0x9du
            val upid = UPID(UPID.Type.TI.ordinal, bytes)
            assert(splice.upid!![0] == upid)

            // Type = Program End
            assert(splice.segmentationType == SegmentationType.ProgramEnd)

            // Segment num = 0
            assert(splice.segmentNum == 0)

            // Segments Expected = 0
            assert(splice.segmentsExpected == 0)
         }

         else -> assert(false)
      }


      when (val splice = info.descriptors!![2]) {
         is SpliceDescriptor.Segmentation -> {
            // Segmentation Descriptor - Length=23
            // Not captured

            // Segmentation Event ID = 0x48000027
            assert(splice.eventId == 0x48000027L)

            // Segmentation Event Cancel Indicator NOT set
            assert(!splice.cancel!!)

            // Delivery Not Restricted flag = 0
            assert(splice.deliveryRestricted!!)

            // Web Delivery Allowed flag = 1
            assert(splice.webDeliveryAllowed!!)

            // No Regional Blackout flag = 1
            assert(splice.noRegionalBlackout!!)

            // Archive Allowed flag = 1
            assert(splice.archiveAllowed!!)

            // Device Restrictions = 3
            assert(splice.deviceRestrictions == SpliceDescriptor.Segmentation.DeviceRestrictions.None)

            // Program Segmentation flag SET
            assert(splice.program!!)
            assert(splice.mode == SpliceMode.Program)

            // UPID Type = Turner Identifier length = 8
            // Turner Identifier = 0x000000002c b2 d7 b3
            assert(splice.upidType == 8)
            assert(splice.upidLength == 8)
            val bytes = UByteArray(8)
            bytes[0] = 0x00u
            bytes[1] = 0x00u
            bytes[2] = 0x00u
            bytes[3] = 0x00u
            bytes[4] = 0x2cu
            bytes[5] = 0xb2u
            bytes[6] = 0xd7u
            bytes[7] = 0xb3u
            val upid = UPID(UPID.Type.TI.ordinal, bytes)
            assert(splice.upid!![0] == upid)

            // Type = Program Start
            assert(splice.segmentationType == SegmentationType.ProgramStart)

            // Segment num = 0
            assert(splice.segmentNum == 0)

            // Segments Expected = 0
            assert(splice.segmentsExpected == 0)
         }

         else -> assert(false)
      }

      // crc32 = 0x8a18869f
      assert(info.crc32 == 0x8a18869fL)
   }

}
