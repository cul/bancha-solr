package edu.columbia.ldpd.text.bancha.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import edu.columbia.ldpd.text.IndexingException;
import edu.columbia.ldpd.text.PageProcessor;
import edu.columbia.ldpd.text.TextPage;
import edu.columbia.ldpd.text.impl.BasePageProcessor;

public abstract class CollectingPageProcessor<Y extends TextPage, T> extends BasePageProcessor<Y, T>
implements PageProcessor<Y>, List<T> {

	List<T> pages;
	public CollectingPageProcessor() {
		pages = new ArrayList<>();
	}

	@Override
	public void cleanUp() throws IndexingException {

	}
	@Override
	public String idFor(Y page) {
		return super.idFor(page);
	}

	
	@Override
	public int size() {
		return pages.size();
	}
	@Override
	public boolean isEmpty() {
		return pages.isEmpty();
	}
	@Override
	public boolean contains(Object o) {
		return pages.contains(o);
	}
	@Override
	public Iterator<T> iterator() {
		return pages.iterator();
	}
	@Override
	public Object[] toArray() {
		return pages.toArray();
	}

	@SuppressWarnings("hiding")
    @Override
	public <T> T[] toArray(T[] a) {
		return pages.toArray(a);
	}
	@Override
	public boolean add(T e) {
		return pages.add(e);
	}
	@Override
	public boolean remove(Object o) {
		return pages.remove(o);
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return pages.containsAll(c);
	}
	@Override
	public boolean addAll(Collection<? extends T> c) {
		return pages.addAll(c);
	}
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return pages.addAll(index, c);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		return pages.removeAll(c);
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		return pages.retainAll(c);
	}
	@Override
	public void clear() {
		pages.clear();
	}
	@Override
	public T get(int index) {
		return pages.get(index);
	}
	@Override
	public T set(int index, T element) {
		return pages.set(index, element);
	}
	@Override
	public void add(int index, T element) {
		pages.add(index, element);
	}
	@Override
	public T remove(int index) {
		return pages.remove(index);
	}
	@Override
	public int indexOf(Object o) {
		return pages.indexOf(o);
	}
	@Override
	public int lastIndexOf(Object o) {
		return pages.lastIndexOf(o);
	}
	@Override
	public ListIterator<T> listIterator() {
		return pages.listIterator();
	}
	@Override
	public ListIterator<T> listIterator(int index) {
		return pages.listIterator(index);
	}
	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return pages.subList(fromIndex, toIndex);
	}

}
