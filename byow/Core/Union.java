package byow.Core;

public class Union {
    int[] parents;

    public Union(int n) {
        parents = new int[n];
        for (int i = 0; i < n; i++) {
            parents[i] = -1;
        }
    }

    public int size(int v1) {
        int root = find(v1);
        return -1 * parents[root];
    }


    public int parent(int v1) {
        return parents[v1];
    }

    public boolean isJoined(int v1, int v2) {
        return find(v1) == find(v2);
    }

    public void join(int v1, int v2) {
        int p1 = find(v1);
        int p2 = find(v2);
        if (p1 == p2) {
            return;
        }
        int v = size(p1) > size(p2) ? p2 : p1;
        int other = size(p1) > size(p2) ? p1 : p2;
        parents[other] -= size(v);
        parents[v] = other;
    }

    public int find(int v1) {
        int temp = v1;
        while (parents[temp] >= 0) {
            temp = parents[temp];
        }
        return temp;
    }

}
