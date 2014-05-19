module types

imports
  lib/runtime-libraries/org.spoofax.meta.runtime.libraries/types/-
  lib/runtime-libraries/org.spoofax.meta.runtime.libraries/relations/-
  
relations

  define transitive <:

type rules

  Var(x) + VarRef(x) + MatchedVar(x): ty
  where definition of x : ty
  
  Con(c, t*) : ty
  where definition of c : (ty*, ty)
    and t* : ty_t*
    and (ty_t* == ty* or ty_t* <: ty*)
    else error "types of sub-terms do not match constructor definition" on c

  List([]) : ListSort("Term")
  
  // l@ListTail(_, _) : ty
  // where
  //   l has expected-type ty
  
  ListTail([x], _) : ListSort(sort)
  where
    x : x-ty
    and x-ty => SimpleSort(sort)
  
  // l@ListTail([x], xs) : ListSort(x-ty)
  // where
  //   x : x-ty
  //   // and xs : ListSort(xs-ty)
  //   // and (x-ty == xs-ty or x-ty <: xs-ty or xs-ty <: x-ty)
  //   else error "incosistent list types" on l 

  SortFunCall(f, _, _) : ty
  where definition of f : (_, ty)

  MapSelect(map, key) : SimpleSort("Term")

  InjDecl(ty, p-ty) :-
  where store ty <: p-ty
  
  NativeSubTypeDecl(_, ty, sup-ty, _) :-
  where store ty <: sup-ty

type rules
  
  Int(i): SimpleSort("Int")
  String(s): SimpleSort("String")
  
  // m@Match(l, r) :-
  // where l : l-ty
  //   and r : r-ty
  //   and (r-ty == l-ty or r-ty <: l-ty or l-ty <: r-ty)
  //     else error "unlikely to succeed" on m
  //  
  // eq@TermEq(l, r) :-
  // where l : l-ty
  //   and r : r-ty
  //   and (r-ty == l-ty or r-ty <: l-ty or l-ty <: r-ty)
  //     else error "unlikely to succeed" on eq
  // 
  // neq@TermNeq(l, r) :-
  // where l : l-ty
  //   and r : r-ty
  //   and (r-ty == l-ty or r-ty <: l-ty or l-ty <: r-ty)
  //     else error "very likely to always succeed" on neq
  
relations

  // this makes no sense but it's here for some generator bug reason
  Var(x) <: VarRef(y)
  where x == y
  
  ListSort(x-ty) <: ListSort(r-ty)
  where x-ty <: r-ty
