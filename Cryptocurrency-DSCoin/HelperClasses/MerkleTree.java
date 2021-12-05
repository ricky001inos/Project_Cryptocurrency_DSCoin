package HelperClasses;

import DSCoinPackage.Transaction;
import java.util.*;

public class MerkleTree {

  // Check the TreeNode.java file for more details
  public TreeNode rootnode;
  public int numdocs;

  void nodeinit(TreeNode node, TreeNode l, TreeNode r, TreeNode p, String val) {
    node.left = l;
    node.right = r;
    node.parent = p;
    node.val = val;
  }

  public String get_str(Transaction tr) {
    CRF obj = new CRF(64);
    String val = tr.coinID;
    if (tr.Source == null)
      val = val + "#" + "Genesis";
    else
      val = val + "#" + tr.Source.UID;

    val = val + "#" + tr.Destination.UID;

    if (tr.coinsrc_block == null)
      val = val + "#" + "Genesis";
    else
      val = val + "#" + tr.coinsrc_block.dgst;

    return obj.Fn(val);
  }

  public String Build(Transaction[] tr) {
    CRF obj = new CRF(64);
    int num_trans = 0;
    while(num_trans < tr.length && tr[num_trans] != null)
      num_trans++;
    List<TreeNode> q = new ArrayList<TreeNode>();
    for (int i = 0; i < num_trans; i++) {
      TreeNode nd = new TreeNode();
      String val = get_str(tr[i]);
      nodeinit(nd, null, null, null, val);
      q.add(nd);
    }
    TreeNode l, r;
    while (q.size() > 1) {
      l = q.get(0);
      q.remove(0);
      r = q.get(0);
      q.remove(0);
      TreeNode nd = new TreeNode();
      String l_val = l.val;
      String r_val = r.val;
      String data = obj.Fn(l_val + "#" + r_val);
      nodeinit(nd, l, r, null, data);
      l.parent = nd;
      r.parent = nd;
      q.add(nd);
    }
    rootnode = q.get(0);
    numdocs = num_trans;
    return rootnode.val;
  }
  public List<Pair<String,String>> QueryDocument(int doc_idx){
  ArrayList<Pair<String,String>> path = new ArrayList<Pair<String,String>>();
  Pair<String, String> to = new Pair<String, String>(null, null);
  TreeNode current = rootnode;
  int x = this.numdocs;
  int j = doc_idx;
  path.add(new Pair<String, String>(this.rootnode.val, null));
  while(x>1){
    path.add(new Pair<String, String>(current.left.val, current.right.val));
    if(j <= (x/2)){
      current = current.left;
    }
    else{
      current = current.right;
      j = j - x/2;
    }
    x = x/2;
  }
  Collections.reverse(path);
  return path;
}
}
