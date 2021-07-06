package com.dsige.dominion.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.Usuario
import com.dsige.dominion.data.viewModel.UsuarioViewModel
import com.dsige.dominion.helper.Util
import com.dsige.dominion.ui.services.SocketServices
import com.dsige.dominion.data.viewModel.ViewModelFactory
import com.dsige.dominion.ui.fragments.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
   private lateinit var usuarioViewModel: UsuarioViewModel
    lateinit var builder: AlertDialog.Builder
    private var dialog: AlertDialog? = null
    private var usuarioId: Int = 0
    private var empresaId: Int = 0
    private var personalId: Int = 0
    private var servicioId: Int = 0
    private var tipo: Int = 0
    private var nombreServicio: String = ""
    private var nombreTipo: String = ""
    private var logout: String = "off"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindUI()
    }

    private fun bindUI() {
        usuarioViewModel =
            ViewModelProvider(this, viewModelFactory).get(UsuarioViewModel::class.java)
        usuarioViewModel.user.observe(this, { u ->
            if (u != null) {
                getUser(u)
                setSupportActionBar(toolbar)
                val toggle = ActionBarDrawerToggle(
                    this@MainActivity,
                    drawerLayout,
                    toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close
                )
                drawerLayout.addDrawerListener(toggle)
                toggle.syncState()
                navigationView.setNavigationItemSelectedListener(this@MainActivity)
                startService(Intent(this, SocketServices::class.java))
                navigationView.menu.clear()
                val menu = navigationView.menu
                val submenu = menu.addSubMenu("Menu Principal")

                usuarioViewModel.getAccesos(u.usuarioId).observe(this, { accesos ->
                    for ((c, a) in accesos.withIndex()) {
                        submenu.add(a.nombre)
                        submenu.getItem(c).setIcon(R.drawable.ic_sync)
                    }
                    val s2 = menu.addSubMenu("")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        s2.setGroupDividerEnabled(true)
                    }

                    s2.add("Lista de Pendientes")
                    s2.getItem(0).setIcon(R.drawable.ic_place)
                    s2.add("Enviar Pendientes")
                    s2.getItem(1).setIcon(R.drawable.ic_send)
                    s2.add("Cerrar Sesión")
                    s2.getItem(2).setIcon(R.drawable.ic_exit)
                    navigationView.invalidate()
                })

                fragmentByDefault()
                message()
            } else {
                goLogin()
            }
        })
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.title) {
            "Sincronizar" -> dialogFunction(
                1, "Se elimiran todos tus avances deseas volver a sincronizar ?"
            )
            "Lista de Pendientes" -> changeFragment(
                GeneralMapFragment.newInstance("", ""), item.title.toString()
            )
            "Lista de Ordenes" -> changeFragment(
                MainFragment.newInstance(
                    usuarioId, empresaId, personalId, servicioId, nombreServicio, tipo, nombreTipo
                ), item.title.toString()
            )
            "Resumen de Ordenes de Trabajo por Proveedor" -> changeFragment(
                ResumenFragment.newInstance(
                    usuarioId, empresaId, personalId, servicioId, nombreServicio
                ), item.title.toString()
            )
            "OT fuera de Plazo" -> changeFragment(
                PlazoFragment.newInstance(
                    usuarioId, empresaId, personalId, servicioId, nombreServicio
                ), item.title.toString()
            )
            "Ubicacion del Personal" -> Util.toastMensaje(this, item.title.toString(), true)
            "Enviar Pendientes" -> dialogFunction(2, "Deseas enviar registros ?")
            "Cerrar Sesión" -> dialogFunction(3, "Deseas Salir ?")
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun load(title: String) {
        builder = AlertDialog.Builder(ContextThemeWrapper(this@MainActivity, R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(this@MainActivity).inflate(R.layout.dialog_login, null)
        builder.setView(view)
        val textViewTitle: TextView = view.findViewById(R.id.textView)
        textViewTitle.text = title
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    private fun closeLoad() {
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
    }

    private fun changeFragment(fragment: Fragment, title: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
        supportActionBar!!.title = title
    }

    private fun fragmentByDefault() {
        supportFragmentManager
            .beginTransaction()
            .replace(
               R.id.content_frame,
                MainFragment.newInstance(
                    usuarioId, empresaId, personalId, servicioId, nombreServicio, tipo, nombreTipo
                )
            )
            .commit()
        supportActionBar!!.title = "Reparación de Veredas"
        //navigationView.menu.getItem(1).isChecked = true
    }

    private fun getUser(u: Usuario) {
        val header = navigationView.getHeaderView(0)
        header.textViewName.text = String.format("%s %s", u.apellidos, u.nombres)
        header.textViewEmail.text = String.format("Cod : %s", u.usuarioId)
        header.textViewVersion.text = String.format("Versión %s", Util.getVersion(this))
        usuarioId = u.usuarioId
        empresaId = u.empresaId
        personalId = u.personalId
        servicioId = u.servicioId
        nombreServicio = u.nombreServicio
        tipo = u.tipo
        nombreTipo = u.nombreTipo
    }

    private fun goLogin() {
        if (logout == "off") {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun message() {
        usuarioViewModel.mensajeSuccess.observe(this, { s ->
            if (s != null) {
                closeLoad()
                if (s == "Close") {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Util.toastMensaje(this, s, true)
                }
            }
        })
        usuarioViewModel.mensajeError.observe(this@MainActivity, { s ->
            if (s != null) {
                closeLoad()
                Util.snackBarMensaje(window.decorView, s)
            }
        })
    }

    private fun dialogFunction(tipo: Int, title: String) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage(title)
            .setPositiveButton("SI") { dialog, _ ->
                when (tipo) {
                    1 -> {
                        load("Sincronizando..")
                        usuarioViewModel.getSync(
                            usuarioId,
                            empresaId,
                            personalId,
                            Util.getVersion(this)
                        )
                    }
                    2 -> {
                        load("Enviando..")
                        usuarioViewModel.sendOt(this)
                    }
                    3 -> {
                        logout = "on"
                        load("Cerrando Session")
                        usuarioViewModel.logout()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.cancel()
            }
        dialog.show()
    }
}