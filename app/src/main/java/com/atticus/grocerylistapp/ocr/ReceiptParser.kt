package com.atticus.grocerylistapp.ocr

object ReceiptParser {
    // "12.34" or "12,34" at end of line
    private val priceRegex = Regex("""(\d+[.,]\d{2})$""")

    // e.g., "500g", "1kg", "750ml", "1l", "2pack", "2pk" (case-insensitive)
    // optional space between number and unit
    private val sizeRegex  = Regex("""(\d+\s*(g|kg|ml|l|pack|pk))""", RegexOption.IGNORE_CASE)

    data class ParsedLine(
        val raw: String,
        val name: String,
        val norm: String,
        val unitPrice: Double,
        val sizeGrams: Int?
    )

    fun parse(lines: List<String>) = lines.mapNotNull { raw0 ->
        // Normalize decimal comma to dot for parsing
        val raw = raw0.replace(",", ".").trim()

        val priceMatch = priceRegex.find(raw) ?: return@mapNotNull null
        val price = priceMatch.groupValues[1].toDouble()

        val left = raw.substring(0, priceMatch.range.first).trim()

        val sizeTok = sizeRegex.find(left)?.value?.lowercase()
            ?.replace(" ", "") // remove any space between number and unit (e.g., "500 g" -> "500g")
        val sizeGrams = toGrams(sizeTok)

        // Remove the size token from the name if present
        val name = sizeTok?.let { left.replace(it, "", ignoreCase = true) } ?: left
        val norm = normalize(name)
        ParsedLine(raw, name.trim(), norm, price, sizeGrams)
    }

    fun normalize(s: String): String =
        s.lowercase()
            .replace(Regex("""[^a-z0-9 ]"""), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()

    private fun toGrams(tok: String?): Int? = when {
        tok == null -> null
        tok.endsWith("kg") -> (tok.dropLast(2).toDoubleOrNull()?.times(1000))?.toInt()
        tok.endsWith("g")  -> tok.dropLast(1).toIntOrNull()
        tok.endsWith("ml") -> tok.dropLast(2).toIntOrNull()
        tok.endsWith("l")  -> (tok.dropLast(1).toDoubleOrNull()?.times(1000))?.toInt()
        else -> null
    }
}
