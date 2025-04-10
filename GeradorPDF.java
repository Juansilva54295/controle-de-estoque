import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GeradorPDF {

    public static void gerarPDF(List<Venda> vendas, String caminhoArquivo, String filtro) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(caminhoArquivo));
            document.open();

            // Título do PDF
            document.add(new Paragraph("Relatório de Vendas - Filtro: " + filtro + "\n\n"));

            // Tabela para exibir os dados
            PdfPTable tabela = new PdfPTable(7); // 7 colunas: ID, Código, Nome, Quantidade, Valor Total, Categoria, Data
            tabela.setWidthPercentage(100);

            // Cabeçalho da tabela
            tabela.addCell("ID");
            tabela.addCell("Código do Produto");
            tabela.addCell("Nome do Produto");
            tabela.addCell("Quantidade");
            tabela.addCell("Valor Total");
            tabela.addCell("Categoria");
            tabela.addCell("Data da Venda");

            // Preenchendo a tabela com os dados das vendas
            for (Venda venda : vendas) {
                tabela.addCell(String.valueOf(venda.getId()));
                tabela.addCell(venda.getCodigoProduto());
                tabela.addCell(venda.getNomeProduto());
                tabela.addCell(String.valueOf(venda.getQuantidade()));
                tabela.addCell(String.format("R$ %.2f", venda.getValorTotal()));
                tabela.addCell(venda.getCategoria());
                tabela.addCell(venda.getDataVenda().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))); // Formata a data
            }

            document.add(tabela);
            document.close();

            System.out.println("PDF gerado com sucesso em: " + caminhoArquivo);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
}