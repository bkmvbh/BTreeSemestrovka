class BTreeNode{

    int[] keys; // Ключи узла
    int MinDeg; // Минимальная степень узла B-дерева
    BTreeNode[] children; // дочерний узел
    int num; // Количество ключей узла
    boolean isLeaf; // Истина, если это листовой узел

    // Конструктор
    public BTreeNode(int deg,boolean isLeaf){

        this.MinDeg = deg;
        this.isLeaf = isLeaf;
        this.keys = new int[2*this.MinDeg-1]; // Узел имеет не более 2 * MinDeg-1 ключей
        this.children = new BTreeNode[2*this.MinDeg];
        this.num = 0;
    }

    // Находим индекс первой позиции, равный или больший, чем ключ
    public int findKey(int key){

        int idx = 0;
        // Условия выхода из цикла: 1.idx == num, то есть сканировать все
        // 2.idx <num, т.е. найти ключ или больше ключа
        while (idx < num && keys[idx] < key)
            ++idx;
        return idx;
    }


    public void remove(int key){

        int idx = findKey(key);
        if (idx < num && keys[idx] == key){ // Найди ключ
            if (isLeaf) // ключ находится в листовом узле
                removeFromLeaf(idx);
            else // ключ отсутствует в листовом узле
                removeFromNonLeaf(idx);
        }
        else{
            if (isLeaf){ // Если узел является листовым узлом, то этот узел не входит в B-дерево
                System.out.printf("The key %d is does not exist in the tree\n",key);
                return;
            }

            // В противном случае удаляемый ключ существует в поддереве с корнем в этом узле

            // Этот флаг указывает, существует ли ключ в поддереве с корнем в последнем дочернем узле узла
            // Когда idx равно num, сравнивается весь узел, и флаг равен true
            boolean flag = idx == num;

            if (children[idx].num < MinDeg) // Когда дочерний узел узла не заполнен, сначала заполняем его
                fill(idx);


            // Если последний дочерний узел был объединен, то он должен был быть объединен с предыдущим дочерним узлом, поэтому мы рекурсивно переходим к (idx-1) -ому дочернему узлу.
            // В противном случае мы рекурсивно переходим к (idx) -ому дочернему узлу, который теперь имеет как минимум ключи наименьшей степени
            if (flag && idx > num)
                children[idx-1].remove(key);
            else
                children[idx].remove(key);
        }
    }

    public void removeFromLeaf(int idx){

        // возвращаемся из idx
        for (int i = idx +1;i < num;++i)
            keys[i-1] = keys[i];
        num --;
    }

    public void removeFromNonLeaf(int idx){

        int key = keys[idx];

        // Если поддерево перед ключом (children [idx]) имеет не менее t ключей
        // Затем находим предшественника key'pred 'в поддереве с корнем в children [idx]
        // Заменить ключ на'pred ', рекурсивно удалить пред в дочерних [idx]
        if (children[idx].num >= MinDeg){
            int pred = getPred(idx);
            keys[idx] = pred;
            children[idx].remove(pred);
        }
        // Если у детей [idx] меньше ключей, чем у MinDeg, проверяем дочерние элементы [idx + 1]
        // Если дочерние элементы [idx + 1] имеют хотя бы ключи MinDeg, в поддереве с корнем дочерние элементы [idx + 1]
        // Находим преемника ключа 'ucc' для рекурсивного удаления succ в дочерних элементах [idx + 1]
        else if (children[idx+1].num >= MinDeg){
            int succ = getSucc(idx);
            keys[idx] = succ;
            children[idx+1].remove(succ);
        }
        else{
            // Если ключи children [idx] и children [idx + 1] меньше MinDeg
            // затем объединяем ключ и дочерние элементы [idx + 1] в дочерние элементы [idx]
            // Теперь children [idx] содержит ключ 2t-1
            // Освобождаем дочерние элементы [idx + 1], рекурсивно удаляем ключ в children [idx]
            merge(idx);
            children[idx].remove(key);
        }
    }

    public int getPred(int idx){ // Узел-предшественник должен найти крайний правый узел из левого поддерева

        // Продолжаем двигаться к крайнему правому узлу, пока не достигнем листового узла
        BTreeNode cur = children[idx];
        while (!cur.isLeaf)
            cur = cur.children[cur.num];
        return cur.keys[cur.num-1];
    }

    public int getSucc(int idx){ // Узел-преемник находится от правого поддерева к левому

        // Продолжаем перемещать крайний левый узел от дочерних [idx + 1], пока не достигнем конечного узла
        BTreeNode cur = children[idx+1];
        while (!cur.isLeaf)
            cur = cur.children[0];
        return cur.keys[0];
    }

    // Заполняем дочерние элементы [idx], у которых меньше ключей MinDeg
    public void fill(int idx){

        // Если предыдущий дочерний узел имеет несколько ключей MinDeg-1, заимствовать из них
        if (idx != 0 && children[idx-1].num >= MinDeg)
            borrowFromPrev(idx);
            // Последний дочерний узел имеет несколько ключей MinDeg-1, заимствовать от них
        else if (idx != num && children[idx+1].num >= MinDeg)
            borrowFromNext(idx);
        else{
            // объединить потомков [idx] и его брата
            // Если children [idx] - последний дочерний узел
            // затем объединить его с предыдущим дочерним узлом, иначе объединить его со следующим братом
            if (idx != num)
                merge(idx);
            else
                merge(idx-1);
        }
    }

    // Заимствуем ключ у потомков [idx-1] и вставляем его в потомки [idx]
    public void borrowFromPrev(int idx){

        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx-1];

        // Последний ключ из дочерних [idx-1] переходит к родительскому узлу
        // ключ [idx-1] из недополнения родительского узла вставляется как первый ключ в дочерних [idx]
        // Следовательно, sibling уменьшается на единицу, а children увеличивается на единицу
        for (int i = child.num-1; i >= 0; --i) // дети [idx] продвигаются вперед
            child.keys[i+1] = child.keys[i];

        if (!child.isLeaf){ // Если дочерний узел [idx] не является листовым, переместите его дочерний узел назад
            for (int i = child.num; i >= 0; --i)
                child.children[i+1] = child.children[i];
        }

        // Устанавливаем первый ключ дочернего узла на ключи текущего узла [idx-1]
        child.keys[0] = keys[idx-1];
        if (!child.isLeaf) // Устанавливаем последний дочерний узел в качестве первого дочернего узла дочерних элементов [idx]
            child.children[0] = sibling.children[sibling.num];

        // Перемещаем последний ключ брата к последнему из текущего узла
        keys[idx-1] = sibling.keys[sibling.num-1];
        child.num += 1;
        sibling.num -= 1;
    }

    // Симметричный с заимствованиемFromPrev
    public void borrowFromNext(int idx){

        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx+1];

        child.keys[child.num] = keys[idx];

        if (!child.isLeaf)
            child.children[child.num+1] = sibling.children[0];

        keys[idx] = sibling.keys[0];

        for (int i = 1; i < sibling.num; ++i)
            sibling.keys[i-1] = sibling.keys[i];

        if (!sibling.isLeaf){
            for (int i= 1; i <= sibling.num;++i)
                sibling.children[i-1] = sibling.children[i];
        }
        child.num += 1;
        sibling.num -= 1;
    }

    // объединить childre [idx + 1] в childre [idx]
    public void merge(int idx){

        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx+1];

        // Вставляем последний ключ текущего узла в позицию MinDeg-1 дочернего узла
        child.keys[MinDeg-1] = keys[idx];

        // ключи: children [idx + 1] скопированы в children [idx]
        for (int i =0 ; i< sibling.num; ++i)
            child.keys[i+MinDeg] = sibling.keys[i];

        // children: children [idx + 1] скопированы в children [idx]
        if (!child.isLeaf){
            for (int i = 0;i <= sibling.num; ++i)
                child.children[i+MinDeg] = sibling.children[i];
        }

        // Перемещаем клавиши вперед, а не зазор, вызванный перемещением ключей [idx] к дочерним [idx]
        for (int i = idx+1; i<num; ++i)
            keys[i-1] = keys[i];
        // Перемещаем соответствующий дочерний узел вперед
        for (int i = idx+2;i<=num;++i)
            children[i-1] = children[i];

        child.num += sibling.num + 1;
        num--;
    }


    public void insertNotFull(int key){

        int i = num -1; // Инициализируем i индексом самого правого значения

        if (isLeaf){ // Когда это листовой узел
            // Находим, куда нужно вставить новый ключ
            while (i >= 0 && keys[i] > key){
                keys[i+1] = keys[i]; // клавиши возвращаются
                i--;
            }
            keys[i+1] = key;
            num = num +1;
        }
        else{
            // Находим позицию дочернего узла, который нужно вставить
            while (i >= 0 && keys[i] > key)
                i--;
            if (children[i+1].num == 2*MinDeg - 1){ // Когда дочерний узел заполнен
                splitChild(i+1,children[i+1]);
                // После разделения ключ в середине дочернего узла перемещается вверх, а дочерний узел разделяется на два
                if (keys[i+1] < key)
                    i++;
            }
            children[i+1].insertNotFull(key);
        }
    }


    public void splitChild(int i ,BTreeNode y){

        // Сначала создаем узел, содержащий ключи MinDeg-1 y
        BTreeNode z = new BTreeNode(y.MinDeg,y.isLeaf);
        z.num = MinDeg - 1;

        // Передаем все атрибуты y в z
        for (int j = 0; j < MinDeg-1; j++)
            z.keys[j] = y.keys[j+MinDeg];
        if (!y.isLeaf){
            for (int j = 0; j < MinDeg; j++)
                z.children[j] = y.children[j+MinDeg];
        }
        y.num = MinDeg-1;

        // Вставляем новый дочерний узел в дочерний узел
        for (int j = num; j >= i+1; j--)
            children[j+1] = children[j];
        children[i+1] = z;

        // Перемещаем ключ по y к этому узлу
        for (int j = num-1;j >= i;j--)
            keys[j+1] = keys[j];
        keys[i] = y.keys[MinDeg-1];

        num = num + 1;
    }


    public void traverse(){
        int i;
        for (i = 0; i< num; i++){
            if (!isLeaf)
                children[i].traverse();
            System.out.printf(" %d",keys[i]);
        }

        if (!isLeaf){
            children[i].traverse();
        }
    }


    public BTreeNode search(int key){
        int i = 0;
        while (i < num && key > keys[i])
            i++;

        if (keys[i] == key) {
            return this;
        }
        if (isLeaf)
            return null;
        return children[i].search(key);
    }
}