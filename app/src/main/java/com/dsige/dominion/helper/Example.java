package com.dsige.dominion.helper;

public class Example{
// extends AppCompatActivity
//    private AlertDialog.Builder builder;
//    private AlertDialog dialog;
//
//    private SendInterfaces sendInterfaces;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//        Realm realm = Realm.getDefaultInstance();
//        final RegistroImplementation registroImp = new RegistroOver(realm);
//
//        builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
//        @SuppressLint("InflateParams") View view = LayoutInflater.from(Example.this).inflate(R.layout.dialog_alert, null);
//
//        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
//        textViewTitle.setText("Enviando...");
//        builder.setView(view);
//
//
//
//        final Observable<RealmResults<Registro>> auditorias = registroImp.getAllRegistroRx(1);
//        String mensaje = "";
//        int suma = 0;
//
//        auditorias.flatMap(new Function<RealmResults<Registro>, ObservableSource<Mensaje>>() {
//
//            @Override
//            public ObservableSource<Mensaje> apply(RealmResults<Registro> registros) throws Exception {
//                return Observable.fromIterable(registros).flatMap(new Function<Registro, ObservableSource<Mensaje>>() {
//                    @Override
//                    public ObservableSource<Mensaje> apply(Registro registro) throws Exception {
//                        MultipartBody.Builder b = new MultipartBody.Builder();
//                        RequestBody requestBody = b.build();
//
//                        return Observable.zip(Observable.just(registro), sendInterfaces.sendRegistroRx(requestBody), new BiFunction<Registro, Mensaje, Mensaje>() {
//                            @Override
//                            public Mensaje apply(Registro registro, Mensaje mensaje) throws Exception {
//
//
//                                if (mensaje != null) {
//                                    registroImp.closeOneRegistro(registro, 0);
//                                    return mensaje;
//                                } else {
//                                    return null;
//                                }
//
//
//                            }
//                        });
//                    }
//                });
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Mensaje>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(Mensaje mensaje) {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//
//
//        dialog = builder.create();
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
//
//    }


}
