package ru.gubber.query;

import java.util.*;

/**
 * Класс для построения дерева классов
 */
public class ClassTree {
    /**
     * Класс сущности
     */
    private Class klass;
    /**
     * Алиас использующийся в HQL запросе. Должен быть уникален для всего дерева
     */
    private String alias;
    /**
     * Ссылается на родительский объект.
     */
    private ClassTree parentKlass;
    /**
     * Таблица дочерних объектов
     */
    private Map childernKlass;

    /**
     * Создаёт новый экземпляр.
     * @param klass Класс сущности
     * @param alias
     */
    public ClassTree(Class klass, String alias)
    {
        this.klass = klass;
        this.alias = alias;
        this.parentKlass = null;
        this.childernKlass = new HashMap();
    }

    public Class getKlass() {
        return klass;
    }

    public void setKlass(Class klass) {
        this.klass = klass;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public ClassTree getParentKlass() {
        return parentKlass;
    }

    public void setParentKlass(ClassTree parentClass) {
        this.parentKlass = parentClass;
    }

    /**
     * Ищет родительский класс для указанного Класса-Сущности и добавляет дочерний класс в дерево
     * @param parent_alias алиас родительского Класса в дереве
     * @param alias алиас Класса-Сущности который добавляется.
     * @param klass Клас сущности
     * @param parent_property свойство в родительском классе для доступа к набору объектов указанной сущности.
     */
    public void addChildrenKlass(String parent_alias, String  alias, Class klass, String parent_property)
    {
        ClassTree tree = getTreeByAlias(parent_alias);
        if (tree==null) throw new RuntimeException("No such leaf in ClassTree with alias = "+ alias);
        if(!containsAlias(alias))
        {
            ClassTree leaf = new ClassTree(klass, alias);
            leaf.setParentKlass(tree);
            tree.addToChildrenKlass(parent_property, leaf);
        }
    }

    /**
     * Добавляет Класс-Сущность в таблицу дочерних объектов.
     * @param prop имя свойсва родительского объекта для доступа к набору объектов указанной сущности.
     * @param leaf дочерний объект для добавления
     */
    private void addToChildrenKlass(String prop, ClassTree leaf)
    {
        childernKlass.put(prop, leaf);
    }

    /**
     * Указывает содиржит ли ветка под данным элементом запрашиваемый алиас.
     * @param alias - алиас, для поиска
     * @return false, если алиас данного элемента, и алиасы всей ветки под данным элементом не содержат запрашиваемый алиас
     * true -  в противном случае.
     */
    public boolean containsAlias(String alias)
    {
        boolean result = false;
        if (this.alias.equals(alias)) result = true;
        else
        {
            Collection collection = childernKlass.values();
            Iterator it = collection.iterator();
            while ( (it.hasNext()) && (!result)) {
                ClassTree tree = (ClassTree) it.next();
                result = tree.containsAlias(alias);
            }
        }
        return result;
    }

    /**
     * Генерирует полную FROM строку для HQL-запроса
     * @return возвращает полную FROM строку для выполнения HQL запроса
     */
    public String getFullFromString(){
        String result = "FROM " + klass.getName() + " AS " + alias +getFromString();
        return result;
    }

    /**
     * Генерирует чать FROM-запроса для дочерних объектов
     * @return LEFT OUTER JOIN во FROM-запрос для дочерних классов.
     */
    private String getFromString(){
        String result = " ";
        Set keys = childernKlass.keySet();
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            ClassTree tree = (ClassTree)childernKlass.get(key);
            result += " LEFT OUTER JOIN " + alias+"."+key +" " + tree.alias;
            result += tree.getFromString();
        }
        return result;
    }

    /**
     * Возвращает ветку с заглавным элементом имеющий заданный алиас
     * @param alias алиас, использующийся для поиска в дереве
     * @return возвращает элемент дерева имеющий заданный алиас
     */
    private ClassTree getTreeByAlias(String alias)
    {
        ClassTree result = null;
        if ( this.getAlias().equals(alias)) result = this;
        else
        {
            Collection collection = childernKlass.values();
            Iterator it  = collection.iterator();
            while ( (it.hasNext()) && (result == null) ) {
                ClassTree tree = (ClassTree) it.next();
                result = tree.getTreeByAlias(alias);
            }
        }
        return result;
    }
}
