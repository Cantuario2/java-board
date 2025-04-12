package edu.cantuario2.ui;

import edu.cantuario2.dto.BoardDetailDTO;
import edu.cantuario2.persistence.entity.BoardEntity;
import edu.cantuario2.service.BoardQueryService;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
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
                case 1 -> createCard();
                case 2 -> moveCard();
                case 3 -> blockCard();
                case 4 -> unlockCard();
                case 5 -> cancelCard();
                case 6 -> showBoard();
                case 7 -> showColumn();
                case 8 -> showCard();
                case 9 -> new MainMenu().execute();
                case 10 -> System.exit(0);
                default -> System.out.println("Opção inválida. Seleciona uma opção do menu.");
            }
        }
    }

    private void createCard() {
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

    private void showColumn() {
    }

    private void showCard() {
    }
}
