package minestack.actions;

import minestack.audit.Audible;

import java.util.Collection;

public interface Action extends Audible {

    Collection<? extends Actor> actors();

}
