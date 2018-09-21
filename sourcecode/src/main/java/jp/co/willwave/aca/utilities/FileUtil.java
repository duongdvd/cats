package jp.co.willwave.aca.utilities;

import com.google.common.collect.Lists;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import jp.co.willwave.aca.exception.CommonException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;

@Component
@PropertySource("classpath:config.properties")
public class FileUtil<T> {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    private static final String ICON_FOLDER = File.separator + "icon" + File.separator;
    public static final String EXCEL_NAME_REGEX_INVALID_CHAR = "[:\\\\/?*\\[\\]]";

    //Save the uploaded file to this folder
    @Value("${cat.media.upload.folder.root}")
    private String FILE_FOLDER;

    public void writeCsv(String filePath, String[] columns, List<T> objects, Class<T> tClass)
            throws CommonException {
        try (Writer writer = Files.newBufferedWriter(Paths.get(filePath))) {
            ColumnPositionMappingStrategy<T> mappingStrategy = new ColumnPositionMappingStrategy<>();
            mappingStrategy.setType(tClass);
            mappingStrategy.setColumnMapping(columns);

            StatefulBeanToCsvBuilder<T> builder = new StatefulBeanToCsvBuilder<>(writer);
            StatefulBeanToCsv<T> beanWriter = builder
                    .withMappingStrategy(mappingStrategy)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();
            beanWriter.write(objects);
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            LOG.error(e.getMessage(), e);
            throw new CommonException(e);
        }
    }

    public List<T> readCsv(String filePath, Class<T> tClass) throws CommonException {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(filePath));
            return readCsvFromReader(reader, tClass);
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    public List<T> readCsv(MultipartFile file, Class<T> tClass) throws CommonException {
        try {
            if (!file.isEmpty()) {
                Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                return readCsvFromReader(reader, tClass);
            }

        } catch (IOException e) {
            throw new CommonException(e);
        }

        return new ArrayList<>();
    }

    private List<T> readCsvFromReader(Reader reader, Class<T> tClass) {
        MappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(tClass);
        CsvToBeanBuilder<T> builder = new CsvToBeanBuilder<>(reader);
        CsvToBean<T> csvToBean = builder
            .withMappingStrategy(strategy)
            .withIgnoreLeadingWhiteSpace(true)
            .build();
        Iterator<T> iterator = csvToBean.iterator();

        return Lists.newArrayList(iterator);
    }

    public String uploadIcon(MultipartFile file) throws CommonException {
        try {
            String folderPath = FILE_FOLDER + File.separator + "icon";
            String fileName = String.valueOf(DateUtil.getCurrentDate().getTime() + "_" + file.getOriginalFilename());
            return (ICON_FOLDER + CryptUtil.encodeBase64(writeFile(file, folderPath, fileName)));
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    private String writeFile(MultipartFile file, String filePath, String fileName) throws IOException {
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        byte[] bytes = file.getBytes();
        String paths = dir.getAbsolutePath() + File.separator + fileName;
        Path path = Paths.get(paths);
        Files.write(path, bytes);
        return paths;
    }

    public void downloadCsvFile(HttpServletResponse response, List<T> list, String[] columns, String csvFileName)
            throws IOException {
        response.setContentType("text/csv");

        // creates mock data
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
        response.setHeader(headerKey, headerValue);

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        csvWriter.writeHeader(columns);
        if (!CollectionUtils.isEmpty(list)) {
            for (T o : list) {
                csvWriter.write(o, columns);
            }
        }

        csvWriter.close();
    }

    public boolean deleteFile(String filePath) {
        File fileToDelete = new File(filePath);
        if (fileToDelete.exists()) {
            return fileToDelete.delete();
        }
        return false;
    }

    public void downloadFileFromKey(String key, HttpServletResponse response) throws CommonException {
        // Remove icon folder to get real file key.
        key = key.replace(ICON_FOLDER, StringUtils.EMPTY);

        // Get File Path from key.
        String filePath = CryptUtil.decodeBase64(key);

        // Get File data and save into HttpServletResponse.
        Path path = Paths.get(filePath);
        try {
            byte[] fileBytes = Files.readAllBytes(path);
            ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes);
            org.apache.commons.io.IOUtils.copy(bis, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    public static void downloadExcel(HttpServletResponse response, String templateName, String responseName, CommonUtil.C<Workbook> consumer) throws Exception {
        Workbook workbook = null;
        try {
            File templateFile = new File(FileUtil.class.getResource(templateName).toURI());
            FileInputStream inputStream = new FileInputStream(templateFile);
            if (templateName.endsWith("xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else if (templateName.endsWith("xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                throw new CommonException("wrong template file type, file name: " + templateName);
            }

            // handler: write data to workbook
            consumer.accept(workbook);

            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + responseName);
            workbook.write(response.getOutputStream()); // Write workbook to response.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null) workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get Cell by row index and column index
     * @param sheet the sheet
     * @param rowIndex the row index
     * @param columnIndex the column index
     * @return the cell
     */
    public static Cell cell(Sheet sheet, int rowIndex, int columnIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        return cell;
    }

    /**
     * get cell by name defined in excel file
     * @param sheet the worksheet
     * @param name the named range (refer {@link Name})
     * @param offsetY the row offset
     * @param offsetX the column offset
     * @return the cell
     */
    public static Cell cell(Sheet sheet, String name, int offsetY, int offsetX) {
        Name n = sheet.getWorkbook().getName(name);
        CellReference cellReference = new CellReference(n == null? name: n.getRefersToFormula());
        return FileUtil.cell(sheet, cellReference.getRow() + offsetY, cellReference.getCol() + offsetX);
    }

    /**
     * get cell by name defined in excel file
     * @param sheet the worksheet
     * @param name the named range (refer {@link Name})
     * @return the cell
     */
    public static Cell cell(Sheet sheet, String name) {
        return cell(sheet, name, 0, 0);
    }

    /**
     * copy style, content from source cell to destination cell
     * @param srcCell the source cell
     * @param destCell the destination cell
     */
    private static void copyCell(Cell srcCell, Cell destCell) {
        CellType cellType = srcCell.getCellTypeEnum();

        switch (cellType) {
            case STRING:
                destCell.setCellValue(srcCell.getRichStringCellValue());
                break;
            case BOOLEAN:
                destCell.setCellValue(srcCell.getBooleanCellValue());
                break;
            case NUMERIC:
                destCell.setCellValue(srcCell.getNumericCellValue());
                break;
            case FORMULA:
                destCell.setCellValue(srcCell.getRichStringCellValue());
                break;
            case ERROR:
                destCell.setCellValue(srcCell.getCellFormula());
                break;
        }

        destCell.setCellStyle(srcCell.getCellStyle());
        if(srcCell.getCellComment() != null) destCell.setCellComment(srcCell.getCellComment());
        if(srcCell.getHyperlink() != null) destCell.setHyperlink(srcCell.getHyperlink());
    }

    /**
     * copy sheet setup (PageSetup, Header, Footer)
     * @param srcSheet source sheet
     * @param destSheet destination sheet
     */
    private static void copySheetSetup(Sheet srcSheet, Sheet destSheet) {
        PrintSetup printSetup = srcSheet.getPrintSetup();
        Header header = srcSheet.getHeader();
        Footer footer = srcSheet.getFooter();

        destSheet.setAutobreaks(srcSheet.getAutobreaks());
        destSheet.setMargin(Sheet.LeftMargin, srcSheet.getMargin(Sheet.LeftMargin));
        destSheet.setMargin(Sheet.RightMargin, srcSheet.getMargin(Sheet.RightMargin));
        destSheet.setMargin(Sheet.TopMargin, srcSheet.getMargin(Sheet.TopMargin));
        destSheet.setMargin(Sheet.BottomMargin, srcSheet.getMargin(Sheet.BottomMargin));
        destSheet.setMargin(Sheet.HeaderMargin, srcSheet.getMargin(Sheet.HeaderMargin));
        destSheet.setMargin(Sheet.FooterMargin, srcSheet.getMargin(Sheet.FooterMargin));

        destSheet.setHorizontallyCenter(srcSheet.getHorizontallyCenter());
        destSheet.setVerticallyCenter(srcSheet.getVerticallyCenter());
        destSheet.setRepeatingColumns(srcSheet.getRepeatingColumns());
        destSheet.setRepeatingRows(srcSheet.getRepeatingRows());

        PrintSetup clonePrintSetup = destSheet.getPrintSetup();
        clonePrintSetup.setCopies(printSetup.getCopies());
        clonePrintSetup.setDraft(printSetup.getDraft());
        clonePrintSetup.setFitHeight(printSetup.getFitHeight());
        clonePrintSetup.setFitWidth(printSetup.getFitWidth());
        clonePrintSetup.setFooterMargin(printSetup.getFooterMargin());
        clonePrintSetup.setHeaderMargin(printSetup.getHeaderMargin());
        clonePrintSetup.setHResolution(printSetup.getHResolution());
        clonePrintSetup.setVResolution(printSetup.getVResolution());
        clonePrintSetup.setLandscape(printSetup.getLandscape());
        clonePrintSetup.setLeftToRight(printSetup.getLeftToRight());
        clonePrintSetup.setNoColor(printSetup.getNoColor());
        clonePrintSetup.setNoOrientation(printSetup.getNoOrientation());
        clonePrintSetup.setNotes(printSetup.getNotes());
        clonePrintSetup.setPageStart(printSetup.getPageStart());
        clonePrintSetup.setPaperSize(printSetup.getPaperSize());
        clonePrintSetup.setScale(printSetup.getScale());
        clonePrintSetup.setUsePage(printSetup.getUsePage());
        clonePrintSetup.setValidSettings(printSetup.getValidSettings());

        Header cloneHeader = destSheet.getHeader();
        cloneHeader.setCenter(header.getCenter());
        cloneHeader.setLeft(header.getLeft());
        cloneHeader.setRight(header.getRight());

        Footer cloneFooter = destSheet.getFooter();
        cloneFooter.setCenter(footer.getCenter());
        cloneFooter.setLeft(footer.getLeft());
        cloneFooter.setRight(footer.getRight());
    }

    /**
     * copy sheet and handle printer
     * @param sheet the sheet template
     * @param consumer the print handler
     * @param datas list of data, each element print on a sheet
     * @param <T> the type of the datas
     */
    public static <T> void copySheet(Sheet sheet, BiConsumer<Sheet, T> consumer, List<T> datas) {
        if (CollectionUtils.isEmpty(datas)) return;
        Workbook workbook = sheet.getWorkbook();
        int sheetIndex = workbook.getSheetIndex(sheet);

        for (T data: datas) {
            Sheet sheetClone = workbook.cloneSheet(sheetIndex);
            // copy sheet setup
            FileUtil.copySheetSetup(sheet, sheetClone);
            // handle printer
            consumer.accept(sheetClone, data);
        }
        //remove sheet template
        workbook.removeSheetAt(sheetIndex);
    }

    /**
     * Copy a range down to an interval addOffsetY
     * @param sheet the worksheet
     * @param name the named range (refer {@link Name})
     * @param addOffsetY the add offset
     * @param consumer the new range handler
     * @param datas the data for new range handler
     * @param <T> the data type of the 'datas'
     */
    public static <T> void verticalCopyRange(Sheet sheet, String name, int addOffsetY, BiConsumer<Range, T> consumer, List<T> datas) {
        if (CollectionUtils.isEmpty(datas)) return;

        T firstData = datas.remove(0);
        Range originalRange = new Range(sheet, name);
        int addOffset = addOffsetY;
        for (T data: datas) {
            Range rangeClone = originalRange.verticalCopy(addOffset);
            // handle printer
            consumer.accept(rangeClone, data);
            addOffset = rangeClone.getShift();
        }
        // print template sheet at last, for reason keep format
        consumer.accept(originalRange, firstData);
    }

    /**
     * Excel area references
     */
    public static class Range {
        /** worksheet */
        private final Sheet sheet;
        /** the distance of the start row of original range and the start row of this range (the cloning range) */
        @Getter
        private final int shift;
        /** original range name */
        private final String name;
        @Getter
        @Setter
        private int index;

        /**
         * @param sheet the worksheet
         * @param shift the shift
         * @param name the name
         */
        public Range(Sheet sheet, int shift, String name) {
            this.sheet = sheet;
            this.shift = shift;
            this.name = name;
        }

        /**
         * @param sheet the worksheet
         * @param name the name
         */
        public Range(Sheet sheet, String name) {
            this.sheet = sheet;
            this.shift = 0;
            this.name = name;
        }

        public Cell cell(String name) {
            return  FileUtil.cell(this.sheet, name, this.shift, 0);
        }

        /**
         * copy a range in vertical
         * @param addOffsetY add offset row
         * @return the cloning range
         */
        public Range verticalCopy(int addOffsetY) {
            return this.copyRange(addOffsetY);
        }

        /**
         * copy a range in vertical
         * @return the cloning range
         */
        public Range verticalCopy() {
            return verticalCopy(0);
        }

        /**
         * TODO
         * copy a range in horizontal
         * @param addOffsetX add offset row
         * @return the cloning range
         */
        public Range horizontalCopy(int addOffsetX) {
            return null;
        }

        /**
         * copy a range in horizontal
         * @return the cloning range
         */
        public Range horizontalCopy() {
            return horizontalCopy(0);
        }

        /**
         * copy range in vertical; TODO copy range in horizontal
         * @param addOffsetY add offset row
         * @return the cloning range
         */
        private Range copyRange(int addOffsetY) {
            AreaReference area = new AreaReference(sheet.getWorkbook().getName(name).getRefersToFormula(), null);
            CellReference firstCell = area.getFirstCell();
            CellReference lastCell = area.getLastCell();
            // row count of original range
            int rowCount = lastCell.getRow() - firstCell.getRow() + 1;
            // the shift of start row of the original range and start row of the cloning range
            int shift = this.shift + addOffsetY + rowCount;
            // copy cell by cell
            for (int y = firstCell.getRow(); y <= lastCell.getRow(); y++) {
                for (int x = firstCell.getCol(); x <= lastCell.getCol(); x++) {
                    Cell srcCell = FileUtil.cell(this.sheet, y, x);

                    // create cloning row when it doesn't existed
                    if (this.sheet.getRow(shift + y) == null) {
                        this.sheet.createRow(shift + y);
                    }
                    Row cloneRow = this.sheet.getRow(shift + y);
                    cloneRow.setHeight(this.sheet.getRow(y).getHeight());

                    Cell cloneCell = cloneRow.createCell(x);
                    FileUtil.copyCell(srcCell, cloneCell);
                }
            }

            // copy merge regions
            for (CellRangeAddress srcRegion : this.sheet.getMergedRegions()) {
                if (firstCell.getRow() <= srcRegion.getFirstRow() && srcRegion.getLastRow() <= lastCell.getRow()) {
                    // srcRegion is fully inside the copied rows
                    final CellRangeAddress destRegion = srcRegion.copy();
                    destRegion.setFirstRow(destRegion.getFirstRow() + shift);
                    destRegion.setLastRow(destRegion.getLastRow() + shift);
                    this.sheet.addMergedRegion(destRegion);
                }
            }
            return new Range(this.sheet, shift, this.name);
        }
    }

    /**
     * replace invalid characters in raw sheet name
     * @param rawName
     * @return
     */
    public static String replaceSheetNameInvalidChar(String rawName) {
        return rawName.replaceAll(EXCEL_NAME_REGEX_INVALID_CHAR, "-");
    }
}
