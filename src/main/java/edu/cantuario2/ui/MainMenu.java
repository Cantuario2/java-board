package edu.cantuario2.ui;

import edu.cantuario2.persistence.entity.BoardColumnEntity;
import edu.cantuario2.persistence.entity.BoardColumnKindEnum;
import edu.cantuario2.persistence.entity.BoardEntity;
import edu.cantuario2.service.BoardQueryService;
import edu.cantuario2.service.BoardService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static edu.cantuario2.persistence.config.ConnConfig.getConnection;

public class MainMenu {
    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("\n::Gerenciador de Boards ::");
        System.out.println("\nEscolha uma opção:");
        int option = -1;
        while (true) {
            System.out.println("1 - Criar um novo board");
            System.out.println("2 - Selecionar um board existente");
            System.out.println("3 - Excluir um board");
            System.out.println("4 - Sair");
            option = scanner.nextInt();
            switch (option) {
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Opção inválida. Seleciona uma opção do menu.");
            }
        }
    }

    private void createBoard() throws SQLException {
        BoardEntity board = new BoardEntity();
        System.out.println("Informe o nome do board:");
        board.setName(scanner.next());

        System.out.println("Se o board terá mais de 3 colunas informe a quantidade ou informe 0: ");
        int additionalColumns = scanner.nextInt();
        List<BoardColumnEntity> columns = new ArrayList<>();
        System.out.println("Informe o nome da coluna inicial do board:");
        String initialColumnName = scanner.next();
        BoardColumnEntity initialColumn = createColumn(initialColumnName, BoardColumnKindEnum.INITIAL, 0);
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Infomre o nome da coluna da tarefa pendente");
            String pendingColumnName = scanner.next();
            BoardColumnEntity pendingColumn = createColumn(pendingColumnName, BoardColumnKindEnum.PENDING, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Infomre o nome da coluna final");
        String finalColumnName = scanner.next();
        BoardColumnEntity finalColumn = createColumn(finalColumnName, BoardColumnKindEnum.FINAL, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Infomre o nome da coluna de cancelamento do board");
        String cancelColumnName = scanner.next();
        BoardColumnEntity cancelColumn = createColumn(cancelColumnName, BoardColumnKindEnum.CANCEL, additionalColumns + 1);
        columns.add(cancelColumn);

        board.setBoardColumns(columns);
        try (Connection conn = getConnection()) {
            BoardService boardService = new BoardService(conn);
            boardService.insert(board);
        }
    }

    private void selectBoard() throws SQLException {
        System.out.println("Informe o id do board a ser selecionado:");
        Long id = scanner.nextLong();
        try (Connection conn = getConnection()) {
            BoardQueryService queryService = new BoardQueryService(conn);
            Optional<BoardEntity> optionalBoardEntity = queryService.findById(id);
            optionalBoardEntity.ifPresentOrElse(bm -> new BoardMenu(bm).execute(), () -> System.out.printf("Não foi encontrado um board com o id %s\n", id));
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Informe o id do board que será excluído");
        Long id = scanner.nextLong();
        try (Connection conn = getConnection()) {
            BoardService boardService = new BoardService(conn);
            if (boardService.delete(id)) {
                System.out.printf("\nO board %s foi excluído", id);
            } else {
                System.out.printf("Não foi encontrado nenhum board com o id %s", id);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kindEnum, final int order) {
        BoardColumnEntity boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kindEnum);
        boardColumn.setOrder(order);
        return boardColumn;
    }
}
