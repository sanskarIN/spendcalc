package `in`.sanskar.spendcalc.domain.export

import org.junit.Assert.assertEquals
import org.junit.Test

class BackupCodecValidationTest {
    private val codec = BackupCodec()

    @Test
    fun `rejects empty and truncated payloads without throwing`() {
        assertEquals(
            BackupDecodeResult.Failure(BackupDecodeError.INVALID_FORMAT),
            codec.decode(""),
        )
        assertEquals(
            BackupDecodeResult.Failure(BackupDecodeError.INVALID_FORMAT),
            codec.decode("SPENDCALC_BACKUP\t1\t1\n"),
        )
    }

    @Test
    fun `rejects oversized payload before parsing records`() {
        val payload = "x".repeat(5_000_001)

        assertEquals(
            BackupDecodeResult.Failure(BackupDecodeError.TOO_LARGE),
            codec.decode(payload),
        )
    }
}
