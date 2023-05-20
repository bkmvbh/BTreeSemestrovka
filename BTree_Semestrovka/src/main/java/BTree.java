class BTree{
    BTreeNode root;
    int MinDeg;

    long timeForSearching = 0;

    // Конструктор
    public BTree(int deg){
        this.root = null;
        this.MinDeg = deg;
    }

    public void traverse(){
        if (root != null){
            root.traverse();
        }
    }

    // Функция для поиска ключа
    public BTreeNode search(int key){
        long t = System.nanoTime();
        BTreeNode returnValue = (root == null ? null : root.search(key));
        long d = System.nanoTime() - t;
        System.out.println("Время выполнения для поиска ключа " + key + ": " + d);
        timeForSearching = d;
        return returnValue;
    }

    public long getTimeForSearching() {
        return timeForSearching;
    }

    public long insert(int key){
        long t = System.nanoTime();
        if (root == null){

            root = new BTreeNode(MinDeg,true);
            root.keys[0] = key;
            root.num = 1;
        }
        else {
            // Когда корневой узел заполнится, дерево станет выше
            if (root.num == 2*MinDeg-1){
                BTreeNode s = new BTreeNode(MinDeg,false);
                // Старый корневой узел становится дочерним узлом нового корневого узла
                s.children[0] = root;
                // Отделяем старый корневой узел и даем ключ новому узлу
                s.splitChild(0,root);
                // Новый корневой узел имеет 2 дочерних узла, переместите туда старый корневой узел
                int i = 0;
                if (s.keys[0]< key)
                    i++;
                s.children[i].insertNotFull(key);

                root = s;
            }
            else
                root.insertNotFull(key);
        }
        long d = System.nanoTime() - t;
        System.out.println("Время выполнения для добавления ключа " + key + ": " + d);
        return d;
    }

    public long remove(int key){
        long t = System.nanoTime();
        if (root == null){
            System.out.println("The tree is empty");
            return 0;
        }

        root.remove(key);

        if (root.num == 0){ // Если у корневого узла 0 ключей
            // Если у него есть дочерний узел, используйте его первый дочерний узел как новый корневой узел,
            // В противном случае установите корневой узел в ноль
            if (root.isLeaf)
                root = null;
            else
                root = root.children[0];
        }
        long d = System.nanoTime() - t;
        System.out.println("Время выполнения для удаления ключа " + key + ": " + d);
        return d;
    }
}