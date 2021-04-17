package toolkit

import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

@DslMarker
annotation class ReadExcelDSL

class SheetAccess(val sheet: Sheet): Sheet by sheet {
    @ReadExcelDSL
    val CellAddress.cell: Cell? get() = sheet.getCellAt(this)

    @ReadExcelDSL
    val Pair<Int, Int>.cell: Cell? get() = sheet.getCellAt(this)

    @ReadExcelDSL
    val String.cell: Cell? get() = CellAddress(this).cell

    @ReadExcelDSL
    operator fun get(row: Int, col: Int): Cell? = getRow(row)?.getCell(col)

    @ReadExcelDSL
    inline operator fun <T> String.invoke(block: Cell.() -> T): T? = cell?.block()

    @ReadExcelDSL
    fun from(row: Int, col: Int): CellAddress = CellAddress(row, col)

    @ReadExcelDSL
    infix fun CellAddress.to(other: CellAddress) = this.spanTo(other).map { it.cell }

    @ReadExcelDSL
    fun CellAddress.to(row: Int, col: Int) = this.spanTo(CellAddress(row, col))

    @ReadExcelDSL
    infix fun Sequence<CellAddress>.and(other: Sequence<CellAddress>): Sequence<CellAddress> = this + other

    @ReadExcelDSL
    val Sequence<CellAddress?>.cellValues get() = map { it?.cell }
}

class WorkbookAccess(val workbook: Workbook): Workbook by workbook {
    inline operator fun <T> String.invoke(block: SheetAccess.() -> T): T = sheet(this, block)
}

@ReadExcelDSL
inline fun <T> readExcel(file: File, block: WorkbookAccess.() -> T): T {
    val workbook: Workbook = file.inputStream().use { XSSFWorkbook(it) }
    return WorkbookAccess(workbook).block()
}

@ReadExcelDSL
inline fun <T> Workbook.sheet(name: String, block: SheetAccess.() -> T): T {
    return SheetAccess(getSheet(name)).block()
}

fun Sheet.rows(range: IntRange) = rowIterator().asSequence().filterIndexed { index, _ -> index in range }

operator fun Sheet.get(rowNum: Int): Row? = getRow(rowNum)
operator fun Row.get(colNum: Int): Cell? = getCell(colNum)

fun Sheet.getCellAt(address: CellAddress) = getRow(address.row)?.getCell(address.column)
fun Sheet.getCellAt(address: Pair<Int, Int>) = getRow(address.first)?.getCell(address.second)

fun Sheet.collectAllAt(vararg cells: CellAddress) = cells.map { getCellAt(it) }
fun Sheet.collectAllAt(vararg cells: Pair<Int, Int>) = cells.map { getCellAt(it) }

fun CellAddress.spanTo(other: CellAddress) = sequence {
    require(row <= other.row){
        "The first cell row can not be greater than from other"
    }
    require(column <= other.column){
        "The first cell column can not be greater than from other"
    }
    for (rowId in row .. other.row){
        for (colId in column .. other.column){
            yield(CellAddress(rowId, colId))
        }
    }
}

fun Pair<Int, Int>.toCellAddress() = CellAddress(first, second)









fun interface HashCollector {
    fun SheetAccess.collect(): Hash
    fun collectHelper(access: SheetAccess): Hash = access.collect()
}

@OptIn(ExperimentalStdlibApi::class)
fun File.collectData(
        vararg toCollect: Pair<String, HashCollector>
): Map<String, Hash> =
        buildMap {
            readExcel(this@collectData){
                toCollect.forEach { (sheetName, collector) ->
                    sheetName {
                        this@buildMap[sheetName] = collector.collectHelper(this)
                    }
                }
            }
        }


fun main() {

    val targ = File("D:\\NextCloud\\IR\\Übungen\\2021_SS\\Solutions\\1\\Solution_1_Handout - Kopie.xlsm")

    readExcel(targ){
        "1) GMAP" {
            val cells = from(8, 1).to(10,2).cellValues.filterNotNull().map { it.numericCellValue }.toList()
            println(hash { updateWithDoubles(cells) }.convertToArrayDeclaration())
        }
        "3) NDCG" {
            val toLoad = from(35, 2).to(37, 8) and from(40, 2).to(41, 8)
            val cells = toLoad.cellValues.mapNotNull { it?.numericCellValue }.toList()
            val callsString = from(43,2).to(43, 8).cellValues.mapNotNull { it?.stringCellValue }.toList()

            println(hash { updateWithDoubles(cells); updateWithStrings(callsString) }.convertToArrayDeclaration())
        }

        "4) Ranking" {
            val cells = from(12,1).to(13, 3).cellValues.mapNotNull { it?.numericCellValue }.toList()
            println(hash { updateWithDoubles(cells) }.convertToArrayDeclaration())
        }

        "5) t-Test" {
            val cell = from(10, 1).cell?.numericCellValue
            println(cell?.toHash()?.convertToArrayDeclaration())
        }

        "6) VSM" {
            val cells = from(20,1).to(21, 3).cellValues.mapNotNull { it?.numericCellValue }.toList()
            println(hash { updateWithDoubles(cells) }.convertToArrayDeclaration())
        }
    }
}
