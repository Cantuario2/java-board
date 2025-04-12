package edu.cantuario2.ui;

import edu.cantuario2.dto.BoardDetailDTO;
import edu.cantuario2.persistence.entity.BoardColumnEntity;
import edu.cantuario2.persistence.entity.BoardColumnKindEnum;
import edu.cantuario2.persistence.entity.BoardEntity;
import edu.cantuario2.persistence.entity.CardEntity;
import edu.cantuario2.service.BoardColumnQueryService;
import edu.cantuario2.service.BoardQueryService;
import edu.cantuario2.service.CardQueryService;
import edu.cantuario2.service.CardService;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;

import static edu.cantuario2.persistence.config.ConnConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {
    private final BoardEntity board;
    private static final Logger logger = Logger.getLogger(BoardMenu.class.getName());

    public void execute() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.printf("Board Menu - %s - Opções:", board.getName());
        int option = -1;
        while (true) {
            System.out.println("\n1 - Criar um card");
            System.out.println("2 - Mover um card");
            System.out.println("3 - Bloquear um card");
            System.out.println("4 - Desbloquear um card");
            System.out.println("5 - Cancelar um card");
            System.out.println("6 - Visualizar board");
            System.out.println("7 - Visualizar colunas com cards");
            System.out.println("8 - Visualizar card");
            System.out.println("9 - Voltar ao menu anterior");
            System.out.println("10 - Sair");
            option = scanner.nextInt();
            switch (option) {
                case 1 -> createCard(scanner);
                case 2 -> moveCard();
                case 3 -> blockCard();
                case 4 -> unlockCard();
                case 5 -> cancelCard();
                case 6 -> showBoard();
                case 7 -> showColumn(scanner);
                case 8 -> showCard(scanner);
                case 9 -> new MainMenu().execute();
                case 10 -> System.exit(0);
                default -> System.out.println("\nOpção inválida. Seleciona uma opção do menu.");
            }
        }
    }

    private void createCard(Scanner scanner) {
        CardEntity card = new CardEntity();
        System.out.println("\nInforme o título do card: ");
        card.setTitle(scanner.next());
        System.out.println("\nInforme a descrição do card: ");
        card.setDescription(scanner.next());
        card.setBoardColumn(board.getInitialColumn());
        try (Connection conn = getConnection()) {
            new CardService(conn).insert(card);
        } catch (SQLException e) {
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "createCard method"));
            logger.severe(e.toString());
        }
    }

    private void moveCard() {
    }

    private void blockCard() {
    }

    private void unlockCard() {
    }

    private void cancelCard() {
    }

    private void showBoard() {
        try (Connection conn = getConnection()) {
            Optional<BoardDetailDTO> optional = new BoardQueryService(conn).showBoardDetails(board.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columns().forEach(c -> {
                    System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount());
                });
            });
        } catch (SQLException e) {
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "showBoard method"));
            logger.severe(e.toString());
        }
    }

    private void showColumn(Scanner scanner) throws SQLException {
        System.out.printf("\nEscolha uma coluna do board %s:", board.getName());
        List<Long> columnsIds = board.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        Long selectedColumn = -1L;
        while (!columnsIds.contains(selectedColumn)) {
            board.getBoardColumns().forEach(c -> System.out.printf("\n%s - %s [%s]", c.getId(), c.getName(), c.getKind()));
            selectedColumn = scanner.nextLong();
        }
        try (Connection conn = getConnection()) {
            Optional<BoardColumnEntity> column = new BoardColumnQueryService(conn).findById(selectedColumn);
            column.ifPresent(col -> {
                System.out.printf("\nColuna %s tipo %s", col.getName(), col.getKind());
                col.getCards().forEach(ca -> System.out.printf("\nCard %s - %s\nDescrição: %s", ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }
    }

    private void showCard(Scanner scanner) {
        System.out.println("\nInforme o id do card a ser visualizado:");
        Long selectedCardId = scanner.nextLong();
        try (Connection conn = getConnection()) {
            new CardQueryService(conn).findById(selectedCardId)
                    .ifPresentOrElse(c -> {
                        System.out.printf("\nCard %s - %s", c.id(), c.title());
                        System.out.printf("\nDescrição: %s", c.description());
                        System.out.printf(c.blocked() ? "\nEstá bloqueado. Motivo: %s" : "\nAtivo", c.blockReason());
                        System.out.printf("\nJá foi bloqueado %s vezes", c.blocksAmount());
                        System.out.printf("\nEstá na coluna %s - %s", c.columnId(), c.columnName());
                    }, () -> System.out.printf("\nNão há card com o id %s", selectedCardId));
        } catch (SQLException e) {
            logger.severe(String.format("Error in %s - %s:", this.getClass().getName(), "showCard method"));
            logger.severe(e.toString());
        }
    }
}
