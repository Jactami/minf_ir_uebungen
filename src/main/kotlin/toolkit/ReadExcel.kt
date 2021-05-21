/*
 * Copyright (c) 2021 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

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
    val Iterable<CellAddress?>.cells get() = map { it?.cell }

    @ReadExcelDSL
    val Iterable<CellAddress?>.cellsNotNull get() = mapNotNull { it?.cell }

    @ReadExcelDSL
    val Sequence<CellAddress?>.cells get() = map { it?.cell }

    @ReadExcelDSL
    val Sequence<CellAddress?>.cellsNotNull get() = mapNotNull { it?.cell }

    @ReadExcelDSL
    operator fun get(row: Int, col: Int): Cell? = getRow(row)?.getCell(col)

    @ReadExcelDSL
    inline operator fun <T> String.invoke(block: Cell.() -> T): T? = cell?.block()

    @ReadExcelDSL
    fun from(address: CellAddress): CellAddress = address

    @ReadExcelDSL
    fun from(row: Int, col: Int): CellAddress = CellAddress(row, col)

    @ReadExcelDSL
    fun from(address: String): CellAddress = CellAddress(address)

    @ReadExcelDSL
    infix fun CellAddress.to(other: CellAddress) = this.spanTo(other)

    @ReadExcelDSL
    fun CellAddress.to(row: Int, col: Int) = this.spanTo(CellAddress(row, col))

    @ReadExcelDSL
    fun CellAddress.to(address: String) = this.spanTo(CellAddress(address))

    @ReadExcelDSL
    infix fun Sequence<CellAddress>.and(other: Sequence<CellAddress>): Sequence<CellAddress> = this + other
}

class WorkbookAccess(val workbook: Workbook): Workbook by workbook {
    @ReadExcelDSL
    inline operator fun <T> String.invoke(block: SheetAccess.() -> T): T = sheet(this, block)
}

fun Workbook.asAccess(): WorkbookAccess =
       if(this is WorkbookAccess) this else WorkbookAccess(this)

fun loadAsExcel(file: File) = XSSFWorkbook(file).asAccess()

@ReadExcelDSL
inline fun <T> useAsExcel(file: File, block: WorkbookAccess.() -> T): T =
        XSSFWorkbook(file).asAccess().use { it.block() }

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