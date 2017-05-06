package com.doctordark.compat.com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import javax.annotation.CheckReturnValue;
import java.util.Iterator;

@GwtCompatible(emulated = true)
public abstract class FluentIterableCompat<E> implements Iterable<E> {
	
	private Iterable<E> iterable;

	FluentIterableCompat(Iterable<E> iterable) {
		this.iterable = iterable;
	}

	@CheckReturnValue
	public static <E> FluentIterableCompat<E> from(Iterable<E> iterable) {
		return (iterable instanceof FluentIterableCompat) ? (FluentIterableCompat<E>) iterable : new FluentIterableCompat<E>(iterable) {
			@Override
			public Iterator<E> iterator() {
				return iterable.iterator();
			}
		};
	}

	@CheckReturnValue
	@Override
	public String toString() {
		return Iterables.toString(this.iterable);
	}

	@CheckReturnValue
	public FluentIterableCompat<E> filter(Predicate<? super E> predicate) {
		return from(Iterables.filter(this.iterable, predicate));
	}

	@CheckReturnValue
	public <T> FluentIterableCompat<T> transform(Function<? super E, T> function) {
		return from(Iterables.transform(this.iterable, function));
	}

	@CheckReturnValue
	public ImmutableList<E> toList() {
		return (ImmutableList<E>) this.iterable;
	}
	
}