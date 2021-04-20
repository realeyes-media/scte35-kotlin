package com.realeyes.scte35

import org.junit.Test
import java.util.*

class MultipleUPIDTest {

    companion object {
        @JvmStatic
        fun decodeBase64(message:String) : ByteArray? {
            return Base64.getDecoder().decode(message)
        }
    }

    /**
     * SCTE35 MID() Multiple UPID types structure as defined in section 10.3.3.4.
     * 2018-07-16 03:00:28 M274P30578915636
     * Hex=0xFC30B100000000000000FFF01005000000007FBF00FE0000003C000000000090028E43554549000000007FFF0000112A880D7A0E1E3330333033303331333433343336333933323337333133383339333233360E5834333735363535343739373036353344373336453636354637343646373936463734363135463645363636433546333233423442363537393344373036323342353636313643373536353344373436463739364637343631360101F2A5C880
     * Base64=/DCxAAAAAAAAAP/wEAUAAAAAf78A/gAAADwAAAAAAJACjkNVRUkAAAAAf/8AABEqiA16Dh4zMDMwMzAzMTM0MzQzNjM5MzIzNzMxMzgzOTMyMzYOWDQzNzU2NTU0Nzk3MDY1M0Q3MzZFNjY1Rjc0NkY3OTZGNzQ2MTVGNkU2NjZDNUYzMjNCNEI2NTc5M0Q3MDYyM0I1NjYxNkM3NTY1M0Q3NDZGNzk2Rjc0NjE2AQHypciA
     */
    @Test fun test_MID_multiple_upid() {
        val message = "/DCxAAAAAAAAAP/wEAUAAAAAf78A/gAAADwAAAAAAJACjkNVRUkAAAAAf/8AABEqiA16Dh4zMDMwMzAzMTM0MzQzNjM5MzIzNzMxMzgzOTMyMzYOWDQzNzU2NTU0Nzk3MDY1M0Q3MzZFNjY1Rjc0NkY3OTZGNzQ2MTVGNkU2NjZDNUYzMjNCNEI2NTc5M0Q3MDYyM0I1NjYxNkM3NTY1M0Q3NDZGNzk2Rjc0NjE2AQHypciA"
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
        assert(info.sectionLength == 177)

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
            // SpliceInsert
            is SpliceCommand.Insert -> {
                // Splice Event ID = 0x00
                assert(command.event.id == 0x00L)

                // Flags OON=1 Prog=0 Duration=1 Immediate=1
                assert(command.event.outOfNetwork!!)
                assert(!command.event.program!!)
                assert(command.event.breakDuration != null)
                assert(command.event.immediateSplice!!)
                assert(command.event.mode == SpliceMode.Immediate)

                // SpliceTime null
                assert(command.event.spliceTime == null)

                // Auto Return
                assert(command.event.breakDuration!!.autoReturn)

                // break duration = 0x3c = 60 seconds
                assert(command.event.breakDuration!!.duration == 0x3cL)

                // Unique Program ID = 0
                assert(command.event.programId!! == 0)

                // Avail Num = 0
                assert(command.event.availNum!! == 0)

                // Avails Expected = 0
                assert(command.event.availsExpected == 0)
            }

            else -> assert(false)
        }

        // Descriptor Loop Length = 25
        // not captured

        when (val splice = info.descriptors!![0]) {
            is SpliceDescriptor.Segmentation -> {
                // Segmentation Descriptor - Length=142
                // Not captured

                // Segmentation Event ID = 0x00
                assert(splice.eventId == 0x00L)

                // Segmentation Event Cancel Indicator NOT set
                assert(!splice.cancel!!)

                // Delivery Not Restricted flag = 1
                assert(!splice.deliveryRestricted!!)

                // Program Segmentation flag SET
                assert(splice.program!!)
                assert(splice.mode == SpliceMode.Program)

                // UPID Type = MID() length = 122
                assert(splice.upidType == 13)
                assert(splice.upidLength == 122)

                // Type = ADS: Advertising Information, Length = 30, Bytes as UTF8 = 303030313434363932373138393236
                val bytes1 = ByteArray(30)
                bytes1[0] = 0x33
                bytes1[1] = 0x30
                bytes1[2] = 0x33
                bytes1[3] = 0x30
                bytes1[4] = 0x33
                bytes1[5] = 0x30
                bytes1[6] = 0x33
                bytes1[7] = 0x31
                bytes1[8] = 0x33
                bytes1[9] = 0x34
                bytes1[10] = 0x33
                bytes1[11] = 0x34
                bytes1[12] = 0x33
                bytes1[13] = 0x36
                bytes1[14] = 0x33
                bytes1[15] = 0x39
                bytes1[16] = 0x33
                bytes1[17] = 0x32
                bytes1[18] = 0x33
                bytes1[19] = 0x37
                bytes1[20] = 0x33
                bytes1[21] = 0x31
                bytes1[22] = 0x33
                bytes1[23] = 0x38
                bytes1[24] = 0x33
                bytes1[25] = 0x39
                bytes1[26] = 0x33
                bytes1[27] = 0x32
                bytes1[28] = 0x33
                bytes1[29] = 0x36
                val upid1 = UPID(UPID.Type.ADS.ordinal, bytes1)
                assert(splice.upid!![0] == upid1)

                // Type = ADS: Advertising Information, Length = 88, Bytes as UTF8 = 437565547970653D736E665F746F796F74615F6E666C5F323B4B65793D70623B56616C75653D746F796F7461
                val bytes2 = ByteArray(88)
                bytes2[0] = 0x34
                bytes2[1] = 0x33
                bytes2[2] = 0x37
                bytes2[3] = 0x35
                bytes2[4] = 0x36
                bytes2[5] = 0x35
                bytes2[6] = 0x35
                bytes2[7] = 0x34
                bytes2[8] = 0x37
                bytes2[9] = 0x39
                bytes2[10] = 0x37
                bytes2[11] = 0x30
                bytes2[12] = 0x36
                bytes2[13] = 0x35
                bytes2[14] = 0x33
                bytes2[15] = 0x44
                bytes2[16] = 0x37
                bytes2[17] = 0x33
                bytes2[18] = 0x36
                bytes2[19] = 0x45
                bytes2[20] = 0x36
                bytes2[21] = 0x36
                bytes2[22] = 0x35
                bytes2[23] = 0x46
                bytes2[24] = 0x37
                bytes2[25] = 0x34
                bytes2[26] = 0x36
                bytes2[27] = 0x46
                bytes2[28] = 0x37
                bytes2[29] = 0x39
                bytes2[30] = 0x36
                bytes2[31] = 0x46
                bytes2[32] = 0x37
                bytes2[33] = 0x34
                bytes2[34] = 0x36
                bytes2[35] = 0x31
                bytes2[36] = 0x35
                bytes2[37] = 0x46
                bytes2[38] = 0x36
                bytes2[39] = 0x45
                bytes2[40] = 0x36
                bytes2[41] = 0x36
                bytes2[42] = 0x36
                bytes2[43] = 0x43
                bytes2[44] = 0x35
                bytes2[45] = 0x46
                bytes2[46] = 0x33
                bytes2[47] = 0x32
                bytes2[48] = 0x33
                bytes2[49] = 0x42
                bytes2[50] = 0x34
                bytes2[51] = 0x42
                bytes2[52] = 0x36
                bytes2[53] = 0x35
                bytes2[54] = 0x37
                bytes2[55] = 0x39
                bytes2[56] = 0x33
                bytes2[57] = 0x44
                bytes2[58] = 0x37
                bytes2[59] = 0x30
                bytes2[60] = 0x36
                bytes2[61] = 0x32
                bytes2[62] = 0x33
                bytes2[63] = 0x42
                bytes2[64] = 0x35
                bytes2[65] = 0x36
                bytes2[66] = 0x36
                bytes2[67] = 0x31
                bytes2[68] = 0x36
                bytes2[69] = 0x43
                bytes2[70] = 0x37
                bytes2[71] = 0x35
                bytes2[72] = 0x36
                bytes2[73] = 0x35
                bytes2[74] = 0x33
                bytes2[75] = 0x44
                bytes2[76] = 0x37
                bytes2[77] = 0x34
                bytes2[78] = 0x36
                bytes2[79] = 0x46
                bytes2[80] = 0x37
                bytes2[81] = 0x39
                bytes2[82] = 0x36
                bytes2[83] = 0x46
                bytes2[84] = 0x37
                bytes2[85] = 0x34
                bytes2[86] = 0x36
                bytes2[87] = 0x31
                val upid2 = UPID(UPID.Type.ADS.ordinal, bytes2)
                assert(splice.upid!![1] == upid2)

                // Type = Distributor Placement Opportunity Start
                assert(splice.segmentationType == SegmentationType.DistributorPlacementOpportunityStart)

                // Segment num = 0
                assert(splice.segmentNum == 1)

                // Segments Expected = 1
                assert(splice.segmentsExpected == 1)
            }

            else -> assert(false)
        }

        // crc32 = 0xF2A5C880
        assert(info.crc32 == 0xF2A5C880)
    }

}