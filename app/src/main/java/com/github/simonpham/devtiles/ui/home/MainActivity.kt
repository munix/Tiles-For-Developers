package com.github.simonpham.devtiles.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.simonpham.devtiles.BuildConfig
import com.github.simonpham.devtiles.R
import com.github.simonpham.devtiles.SingletonInstances
import com.github.simonpham.devtiles.TileInfo
import com.github.simonpham.devtiles.ui.common.AdapterModel
import com.github.simonpham.devtiles.ui.common.HeaderModel
import com.github.simonpham.devtiles.ui.common.ItemHeaderViewHolder
import com.github.simonpham.devtiles.ui.common.MixAdapter
import com.github.simonpham.devtiles.util.showAboutDialog
import com.github.simonpham.devtiles.util.showPermissionWizard
import com.github.simonpham.devtiles.util.toast
import com.github.simonpham.devtiles.util.viewChangelog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val adapter: MixAdapter by lazy {
        MixAdapter.Builder {
            withModel<HeaderModel> { ItemHeaderViewHolder.Factory() }
            withModel<TileModel> { ItemTileToggleViewHolder.Factory() }
        }.build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = SingletonInstances.getSharedPrefs()
        if (sharedPrefs.isFirstLaunch) {
            sharedPrefs.isFirstLaunch = false
            showPermissionWizard(this)
        }

        setContentView(R.layout.activity_main)
        supportActionBar?.title = getString(R.string.app_title)

        recyclerView.adapter = adapter

        adapter.setData(makeAdapterData())

        if (sharedPrefs.lastKnownVersionCode < BuildConfig.VERSION_CODE) {
            viewChangelog(this)
            sharedPrefs.lastKnownVersionCode = BuildConfig.VERSION_CODE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_action, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_wizard -> {
                showPermissionWizard(this)
                return true
            }
            R.id.menu_settings -> {
                // settings
                toast("Not implemented!")
                return true
            }
            R.id.menu_about -> {
                showAboutDialog(this)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun makeAdapterData(): List<AdapterModel> {
        val adapterModels = mutableListOf<AdapterModel>()

        val nonRootTiles = mutableListOf<TileInfo>()
        val magicTiles = mutableListOf<TileInfo>()

        TileInfo.values().forEach { tile ->
            if (tile.isMagicRequired) {
                magicTiles.add(tile)
            } else {
                nonRootTiles.add(tile)
            }
        }

        adapterModels.add(HeaderModel(getString(R.string.header_rootless_tiles)))
        adapterModels.addAll(nonRootTiles.map {
            TileModel(it)
        })

        adapterModels.add(HeaderModel(getString(R.string.header_magic_required_tiles)))
        adapterModels.addAll(magicTiles.map {
            TileModel(it)
        })

        return adapterModels
    }
}
