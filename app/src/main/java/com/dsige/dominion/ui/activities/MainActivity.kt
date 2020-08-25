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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.Usuario
import com.dsige.dominion.data.viewModel.UsuarioViewModel
import com.dsige.dominion.helper.Util
import com.dsige.dominion.ui.fragments.MainFragment
import com.dsige.dsigeventas.data.viewModel.ViewModelFactory
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
    lateinit var usuarioViewModel: UsuarioViewModel
    lateinit var builder: AlertDialog.Builder
    var dialog: AlertDialog? = null
    var usuarioId: Int = 0
    var logout: String = "off"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindUI()
    }

    private fun bindUI() {
        usuarioViewModel =
            ViewModelProvider(this, viewModelFactory).get(UsuarioViewModel::class.java)
        usuarioViewModel.user.observe(this, Observer { u ->
            if (u != null) {
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

                navigationView.menu.clear()
                val menu = navigationView.menu
                val submenu = menu.addSubMenu("Menu Principal")

                usuarioViewModel.getAccesos(u.usuarioId).observe(this, Observer { accesos ->
                    for ((c, a) in accesos.withIndex()) {
                        submenu.add(a.nombre)
                        submenu.getItem(c).setIcon(R.drawable.ic_sync)
                    }
                    val s2 = menu.addSubMenu("")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        s2.setGroupDividerEnabled(true)
                    }
                    s2.add("Enviar Pendientes")
                    s2.getItem(0).setIcon(R.drawable.ic_send)
                    s2.add("Cerrar Sesión")
                    s2.getItem(1).setIcon(R.drawable.ic_exit)
                    navigationView.invalidate()
                })
                getUser(u)
//                fragmentByDefault()
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
            "Sincronizar" -> Util.toastMensaje(this, item.title.toString())
            "Lista de Ordenes" -> changeFragment(
                MainFragment.newInstance(usuarioId, 1), item.title.toString()
            )
            "Resumen de Ordenes de Trabajo por Proveedor" -> changeFragment(
                MainFragment.newInstance(usuarioId, 2), item.title.toString()
            )
            "OT fuera de Plazo" -> changeFragment(
                MainFragment.newInstance(usuarioId, 3), item.title.toString()
            )
            "Ubicacion del Personal" -> Util.toastMensaje(this, item.title.toString())
//            R.id.reparacion -> changeFragment(
//                MainFragment.newInstance(1, usuarioId),
//                "Reparación de Veredas"
//            )
            "Enviar Pendientes" -> dialogSend()
            "Cerrar Sesión" -> dialogLogout()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    private fun goActivity(i: Intent) {
        startActivity(i)
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
            .replace(R.id.content_frame, MainFragment.newInstance(usuarioId, 1))
            .commit()
        supportActionBar!!.title = "Reparación de Veredas"
//        navigationView.menu.getItem(1).isChecked = true
    }

    private fun getUser(u: Usuario) {
        val header = navigationView.getHeaderView(0)
        header.textViewName.text = u.nombres
        header.textViewEmail.text = String.format("Cod : %s", u.usuarioId)
        usuarioId = u.usuarioId
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
        usuarioViewModel.mensajeSuccess.observe(this, Observer { s ->
            if (s != null) {
                closeLoad()
                if (s == "Close") {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Util.toastMensaje(this, s)
                }
            }
        })
        usuarioViewModel.mensajeError.observe(this@MainActivity, Observer { s ->
            if (s != null) {
                closeLoad()
                Util.snackBarMensaje(window.decorView, s)
            }
        })
    }

    private fun dialogLogout() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage("Deseas Salir ?")
            .setPositiveButton("SI") { dialog, _ ->
                logout = "on"
                load("Cerrando Session")
                usuarioViewModel.logout(usuarioId.toString())
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.cancel()
            }
        dialog.show()
    }

    private fun dialogSend() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage("Deseas enviar registros ?")
            .setPositiveButton("SI") { dialog, _ ->
                load("Enviando..")
                usuarioViewModel.sendData(this)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.cancel()
            }
        dialog.show()
    }
}