import java.util.*;

public class Main {
  public static void main(String[] args) {
    /*
     * Examples from the DB theory slides
     */

    Set<String> U = new HashSet<>(Set.of("A", "B", "C", "D", "E"));  // Relation U(A,B,C,D,E)
    FD f1 = new FD(Set.of("A", "E"), Set.of("D")); // AE --> D
    FD f2 = new FD(Set.of("A", "B"), Set.of("C")); // AB --> C
    FD f3 = new FD(Set.of("D"), Set.of("B")); // D --> B
    FDSet fdsetU = new FDSet(f1, f2, f3);
    System.out.println("Final BCNF Schemas: " + Normalizer.BCNFDecompose(U, fdsetU));
  }
}
