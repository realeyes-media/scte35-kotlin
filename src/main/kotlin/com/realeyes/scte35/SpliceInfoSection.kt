package com.realeyes.scte35

/**
 * splice_info_section()
 */
class SpliceInfoSection {
   var tableId: Int? = null
      internal set

   var sectionSyntaxIndicator : Boolean? = null
      internal set

   var privateIndicator : Boolean? = null
      internal set

   var sectionLength: Int ? = null
      internal set

   var protocolVersion: Int? = null
      internal set

   var encrypted: Boolean? = null
      internal set

   var encryptionAlgorithm : EncryptionAlgorithm? = null
      internal set

   var ptsTypeAdjustment: PTSType? = null
      internal set

   var cwIndex: Int? = null
      internal set

   var tier: Int? = null
      internal set

   var command: SpliceCommand? = null
      internal set

   var descriptors : Array<SpliceDescriptor>? = null
      internal set

   var stuffing : Int? = null
      internal set

   var ecrc32 : Long? = null
      internal set

   var crc32 : Long? = null
      internal set

}


/**
 * Return the first of [descriptors][com.realeyes.scte35.SpliceInfoSection.descriptors] if both exist, null otherwise.
 *
 * @return the first of [descriptors][com.realeyes.scte35.SpliceInfoSection.descriptors] if both exist, null otherwise.
 */
fun SpliceInfoSection.firstDescriptor() : SpliceDescriptor? {
   val descriptors = this.descriptors
   return if (descriptors != null && descriptors.size > 0) descriptors.get(0) else null
}
