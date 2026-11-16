import java.util.HashSet;
import java.util.Set;

/**
 * This class provides static methods for performing normalization
 * 
 * @author Esther Mosaku
 * @version November 15th, 2026
 */
public class Normalizer {

  /**
   * Performs BCNF decomposition
   * 
   * @param rel   A relation (as an attribute set)
   * @param fdset A functional dependency set
   * @return a set of relations (as attribute sets) that are in BCNF
   */
  public static Set<Set<String>> BCNFDecompose(Set<String> rel, FDSet fdset) {
    Set<Set<String>> result = new HashSet<>();

    if (isBCNF(rel, fdset)){
      result.add(rel);
      return result;
    }

    FDSet nonTrivial = new FDSet();
    Set<Set<String>> listSuperKeys = findSuperkeys(rel, fdset);

    Set<String> tempRel = new HashSet<>(rel);

    Set<String> part1 = new HashSet<>();
    Set<String> part2 = new HashSet<>();

    for (FD each: fdset){
      if (!each.getLeft().containsAll(each.getRight())){
        nonTrivial.add(each);
      }
    }

    for (FD each: nonTrivial){
      if (!listSuperKeys.contains(each.getLeft())){
        System.out.println("*** Splitting on "+ each + " ***");
        part1.addAll(each.getLeft());
        part1.addAll(each.getRight());
        System.out.println("Left schema = "+ part1);
        FDSet fdSet1 = projectFD(part1, fdset);
        System.out.println("Left Schema's superkey: "+ findSuperkeys(part1, fdSet1));

        //Remove the right part of the violator from the copy of the original relation
        tempRel.removeAll(each.getRight());
        tempRel.addAll(each.getLeft());
        part2.addAll(tempRel);
        System.out.println("Right Schema = "+ part2);
        FDSet fdSet2 = projectFD(part2, fdset);
        System.out.println("Left Schema's superkey: "+ findSuperkeys(part2, fdSet2));

        result.addAll(BCNFDecompose(part1, fdSet1));
        result.addAll(BCNFDecompose(part2, fdSet2));
      }
      return result;
    }
    return result;
  }

  /**
   * Helper function for the projection after the first split into two relations
   * 
   * @param part the set of string of attributes that we want to redistribute
   * @param fdset the set of FDs
   * @return the FDset of the new projection
   */
  public static FDSet projectFD(Set<String> part, FDSet fdset){
    FDSet fdSet1 = new FDSet();
    Set<Set<String>> tempPowerSet = FDUtil.powerSet(part);
    
    for (Set<String> each: tempPowerSet){
      if (each.size() == 0 || each.size() == part.size()){
        continue;
      }
      Set<String> eachClosure = attClosure(each, fdset);
      Set<String> beta = attClosure(each, fdset);
      for (String col: eachClosure){
        if (!part.contains(col)){
          beta.remove(col);
        }
      }
      beta.removeAll(each);
      if (beta.isEmpty()){
        continue;
      }
      else{
        FD newFd = new FD(each, beta);
        fdSet1.add(newFd);
      }
    }
    return fdSet1;
  }

  
  /**
   * Tests whether the given relation is in BCNF. A relation is in BCNF iff the
   * left-hand attribute set of all nontrivial FDs is a super key.
   * 
   * @param rel   A relation (as an attribute set)
   * @param fdset A functional dependency set
   * @return true if the relation is in BCNF with respect to the specified FD set
   */
  public static boolean isBCNF(Set<String> rel, FDSet fdset) {
    // it is in BCNF iff it is trivial or a non trivial superkey for the left part of the FD
    Set<Set<String>> listSuperKeys = findSuperkeys(rel, fdset);
    for (FD each: fdset){
      if (!(!(each.getLeft().containsAll(each.getRight())) && listSuperKeys.contains(each.getLeft()))){
        return false;
      }
    }
    return true;
  }

  /**
   * This method returns a set of super keys
   * 
   * @param rel   A relation (as an attribute set)
   * @param fdset A functional dependency set
   * @return a set of super keys
   */
  public static Set<Set<String>> findSuperkeys(Set<String> rel, FDSet fdset) {
    Set<Set<String>> keys = new HashSet<>();
    FDSet copy = new FDSet(fdset);
    Set<String> tempList = new HashSet<>();

    Set<Set<String>> tempPowerSet = FDUtil.powerSet(rel);

    for (FD funcDep: copy){
      tempList.addAll(funcDep.getLeft());
      tempList.addAll(funcDep.getRight());
    }

    if (!rel.containsAll(tempList)){
      throw new IllegalArgumentException("There is a functional dependency that does not exist in the ");
    }

    for(Set<String> each: tempPowerSet){
      if (attClosure(each, fdset).equals(rel)){
        keys.add(each);
      }
    }
    return keys;
  }


  /**
   * Helper function: The attribute closure algorithm to find the attribute for each attribute subset
   * 
   * @param subset the attribute subset for which we want to find the attribute closure
   * @param fdSet the set of FDs
   * @return a set of dtring containing all of the attributes under the closure.
   */
  public static Set<String> attClosure(Set<String> subset, FDSet fdSet){
    Set<String> tempSet = new HashSet<>(subset);

    for (FD each: fdSet){
      if (tempSet.containsAll(each.getLeft()) && !tempSet.containsAll(each.getRight())){
          tempSet.addAll(each.getRight());
        }
    
    }
    return tempSet;
  }

}
