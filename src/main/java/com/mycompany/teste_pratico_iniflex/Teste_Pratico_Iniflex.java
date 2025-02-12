package com.mycompany.teste_pratico_iniflex;
/**
 *
 * @author ryanmarques1
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.ZoneId;



import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.*;


import java.time.LocalDate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.Period;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
     


class Pessoa {
    private String nome;
    private LocalDate dataNascimento;

    public Pessoa(String nome, LocalDate dataNascimento) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
    }

    public String getNome() {
        return this.nome;
    }

    public LocalDate getDataNasc() {
        return this.dataNascimento;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDataNasc(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String retornaPessoa() {
        
        return "Pessoa{ nome= " + this.nome + ", data de Nascimento= " + this.dataNascimento + " }";
    }
}

class Funcionario extends Pessoa {
    private BigDecimal salario;
    private String funcao;

    public Funcionario(String nome, LocalDate dataNascimento, BigDecimal salario, String funcao) {
        super(nome, dataNascimento);
        this.salario = salario;
        this.funcao = funcao;
    }

    public BigDecimal getSalario() {
        return this.salario;
    }

    public String getFuncao() {
        return this.funcao;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formataData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return String.format("| Nome: %-15s | Data de Nascimento: %-12s | Salário: %10s | Função: %-15s |", 
                getNome(), 
                getDataNasc().format(formataData), 
                getSalario().setScale(2, RoundingMode.HALF_UP), 
                getFuncao());
    }
}

///-------------------------------------
public class Teste_Pratico_Iniflex {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8)); ///Utilizar caracteres especiais.
        
        AtomicInteger numFuncs = new AtomicInteger(1); ///inteiro atomico para conseguir incrementar dentro do forEach
        //3.1 inserir funcionarios
        String caminho = "funcionarios.xls";
        
        List<Funcionario> Funcionarios = lerDados(caminho);
        
        if(Funcionarios.isEmpty()){
            System.out.println("Arquivo está vazio!");
            return;
        }
        
        System.out.println("\nPlanilha em estado inicial!.\n");
        Funcionarios.forEach(System.out::println); ///printando a planilha
        
        ///3.2 removendo o joão
        int tamanhoLista = Funcionarios.size();
        for(int i = 0; i < tamanhoLista; i++){
            Funcionario auxiliar = Funcionarios.get(i);
            if(auxiliar.getNome().equals("Joao")){
                Funcionarios.remove(auxiliar);
                break;
            }
        }
        
        ///3.3 Imprimindo os funcionarios
        System.out.println("\n");
        Funcionarios.forEach(System.out::println); ///printando a planilha
        ///3.4 Aumento salario
        Funcionarios.forEach((salarioFunc) -> {
            BigDecimal salarioNovo = salarioFunc.getSalario().multiply(new BigDecimal("1.10")); ///retorna salario multiplicando por 1.10
            salarioNovo = salarioNovo.setScale(2,RoundingMode.HALF_UP);
            salarioFunc.setSalario(salarioNovo); /// atualiza Salario com o novo salario.
        });
        System.out.println("\n");
        Funcionarios.forEach(System.out::println); ///printando a planilha
        
        ///3.5
        Map<String, List<Funcionario>> funcPorFuncao = Funcionarios.stream().collect
        (Collectors.groupingBy(Funcionario::getFuncao));
        
        ///3.6
        System.out.println("\nFuncionários por função impressos abaixo: \n");
        funcPorFuncao.forEach((funcao, listaFuncs) -> {
            System.out.println("Função " + numFuncs.getAndIncrement() + ": " + funcao);
            listaFuncs.forEach(System.out::println);
        });
        
        ///3.7
        System.out.println("\nFuncionário(s) que fazem aniversário no mês 10 ou 12.\n");
        Funcionarios.forEach(funcionario ->{
            int mes = funcionario.getDataNasc().getMonthValue();
            if(mes == 10 || mes == 12)
                System.out.println(funcionario);
        });
        ///3.8  Imprimir o funcionário com a maior idade, exibir os atributos: nome e idade.
        Funcionario maiorIdade = Collections.min(Funcionarios, Comparator.comparing(Funcionario::getDataNasc));
        System.out.println("\nO funcionário mais velho é: \n");
        
        System.out.println("Nome: " + maiorIdade.getNome() + ", Idade: " + Period.between(maiorIdade.getDataNasc()
        , LocalDate.now()).getYears());
        ///3.9
        System.out.println("\nImprimindo os funcionários em ordem alfabética.\n");
        Funcionarios.stream().sorted((f1,f2) -> 
        f1.getNome().compareTo(f2.getNome())).collect(Collectors.toList())
        .forEach(System.out::println);
       
        ///3.10
        System.out.println("\nImprimir total de salários dos funcionários.\n");
        BigDecimal somaSalario = Funcionarios.stream().collect(Collectors
                .reducing(BigDecimal.ZERO, Funcionario::getSalario, BigDecimal::add));
        
        somaSalario = somaSalario.setScale(2,RoundingMode.HALF_UP);
        System.out.println("A soma total dos salários deu: " + somaSalario);
        
        ///3.11
        System.out.println("\nImprimindo salários mínimos de cada funcionário.\n");
        BigDecimal salarioMin = new BigDecimal("1212.00");
        Funcionarios.stream().map(funcionario -> {
            BigDecimal salariosMinsFuncs = funcionario.getSalario().divide(salarioMin, 2, RoundingMode.HALF_UP);
            return "O funcionário " + funcionario.getNome() + 
                    " recebe "+ ": " + salariosMinsFuncs +  " salários minimos";
        })
        .forEach(System.out::println);
    }
    
    
    /*
    Funções auxiliares abaixo.
    */
    
    public static List<Funcionario> lerDados(String caminho) {
        List<Funcionario> Funcionarios = new ArrayList<>();
    
        try (FileInputStream arquivo = new FileInputStream(new File(caminho));
             Workbook arquivoPlanilha = new HSSFWorkbook(arquivo)) { // Usando HSSFWorkbook para arquivos .xls

            Sheet planilha = arquivoPlanilha.getSheetAt(0); // posição 0 da planilha
            
            for (Row coluna : planilha) {
                if (coluna.getRowNum() == 0) continue; // Ignora o cabeçalho
                String nome = coluna.getCell(0).getStringCellValue();
                LocalDate dataNascimento = converterParaData(coluna.getCell(1));
                BigDecimal salario = converterParaBigDecimal(coluna.getCell(2));
                String funcao = coluna.getCell(3).getStringCellValue();
                
                Funcionarios.add(new Funcionario(nome, dataNascimento, salario, funcao));
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
        return Funcionarios;
    }

    //converte o campo data nascimento na planilha para data
    private static LocalDate converterParaData(Cell celula) {
        if (celula.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(celula)) {
            return celula.getDateCellValue().toInstant()
                      .atZone(ZoneId.systemDefault())
                      .toLocalDate();
        } else if (celula.getCellType() == CellType.STRING) {
            DateTimeFormatter dataFormatada = DateTimeFormatter.ofPattern("dd/MM/yyyy"); ///converte para DiaMêsAno
            return LocalDate.parse(celula.getStringCellValue(), dataFormatada);
        }
        throw new IllegalArgumentException("Erro na célula de data" + celula);
    }

    // converte o campo salario para Bigdecimal
    private static BigDecimal converterParaBigDecimal(Cell celula) {
        if (celula.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(celula.getNumericCellValue());
        } else if (celula.getCellType() == CellType.STRING) {
            return new BigDecimal(celula.getStringCellValue().replace(",", ".")); //coloca ponto se houver virgula
        }
        throw new IllegalArgumentException("Erro na célula de salário" + celula);
    }
}


