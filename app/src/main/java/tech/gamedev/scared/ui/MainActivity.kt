package tech.gamedev.scared.ui

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import tech.gamedev.scared.R
import tech.gamedev.scared.adapters.SwipeSongAdapter
import tech.gamedev.scared.data.models.Song
import tech.gamedev.scared.data.models.Video
import tech.gamedev.scared.exoplayer.isPlaying
import tech.gamedev.scared.exoplayer.toSong
import tech.gamedev.scared.other.Status
import tech.gamedev.scared.ui.viewmodels.MainViewModel
import tech.gamedev.scared.ui.viewmodels.StoryViewModel
import tech.gamedev.scared.ui.viewmodels.VideoViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private val storyViewModel: StoryViewModel by viewModels()

    private val videoViewModel: VideoViewModel by viewModels()

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager

    private var curPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)


        //SUBSCRIBE TO VIEWMODEL OBSERVERS
        subscribeToObservers()
        storyViewModel.stories.observe(this) {}

        videoViewModel.documentaries.observe(this) {}
        videoViewModel.redditVideos.observe(this) {}


        //SET PLAY AND SWIPE VIEWPAGER
        vpSong.adapter = swipeSongAdapter

        vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (playbackState?.isPlaying == true) {
                    mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position])
                } else {
                    curPlayingSong = swipeSongAdapter.songs[position]
                }
            }
        })

        ivPlayPause.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        swipeSongAdapter.setItemClickListener {
            navHostFragment.findNavController().navigate(
                R.id.globalActionToSongFragment
            )
        }

        //BOTTOM NAVIGATION SETUP
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
        bottomNavigationView.setOnNavigationItemReselectedListener {/* NO-OP */ }

        //BOTTOM NAVIGATION HIDE WHEN NOT NEEDED
        navHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.featuredFragment, R.id.audioFragment, R.id.bookFragment, R.id.videoFragment, R.id.profileFragment -> {
                        bottomNavigationView.visibility = View.VISIBLE
                        showBottomBar()
                    }

                    else -> {
                        hideBottomBar()
                        bottomNavigationView.visibility = View.GONE
                    }
                }
            }

        //END BOTTOM NAVIGATION SETUP
    }

    private fun hideBottomBar() {
        ivCurSongImage.isVisible = false
        vpSong.isVisible = false
        ivPlayPause.isVisible = false
        cvCurSongImage.isVisible = false
    }

    private fun showBottomBar() {
        ivCurSongImage.isVisible = true
        vpSong.isVisible = true
        ivPlayPause.isVisible = true
        cvCurSongImage.isVisible = true
    }

    private fun switchViewPagerToCurrentSong(song: Song) {
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
        if (newItemIndex != -1) {
            vpSong.currentItem = newItemIndex
            curPlayingSong = song
        }
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(this) {
            it?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        result.data?.let { songs ->
                            swipeSongAdapter.songs = songs
                            if (songs.isNotEmpty()) {
                                glide.load((curPlayingSong ?: songs[0]).imageUrl)
                                    .into(ivCurSongImage)
                            }
                            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
                        }
                    }
                    Status.ERROR -> Unit
                    Status.LOADING -> Unit
                }
            }
        }
        mainViewModel.curPlayingSong.observe(this) {
            if (it == null) return@observe

            curPlayingSong = it.toSong()
            glide.load(curPlayingSong?.imageUrl).into(ivCurSongImage)
            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
        }
        mainViewModel.playbackState.observe(this) {
            playbackState = it
            ivPlayPause.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
        }
        mainViewModel.isConnected.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.ERROR -> Snackbar.make(
                        rootLayout,
                        result.message ?: "An unknown error occured",
                        Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }
            }
        }
        mainViewModel.networkError.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.ERROR -> Snackbar.make(
                        rootLayout,
                        result.message ?: "An unknown error occured",
                        Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }
            }
        }
    }

    private fun uploadData(){

        val db = FirebaseFirestore.getInstance()

        val audioFiles = ArrayList<Song>()

        audioFiles.add(Song(
                "1",
                "3 Scary True Stories of Homeless People.",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/3%20Scary%20True%20Stories%20of%20Homeless%20People.mp3?alt=media&token=225ee44c-3555-488e-800a-745e03aa22b8",
                "https://images-na.ssl-images-amazon.com/images/I/61P6BflbKHL._AC_UY445_.jpg"

        ))
        audioFiles.add(Song(
                "2",
                "A Warning To Those Accessing The Shadow Web",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/A%20Warning%20To%20Those%20Accessing%20The%20Shadow%20Web%20%20Creepypasta.mp3?alt=media&token=69bf1cdb-6c81-4b04-8b99-faefa803dd39",
                "https://www.channelpartnersonline.com/files/2017/08/Dark-Web.jpg"

        ))

        audioFiles.add(Song(
                "3",
                "Camera 23",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/Camera%2023%20%20Creepypasta.mp3?alt=media&token=8432330d-b6f0-4153-a707-59e775359e11",
                "https://www.komando.com/wp-content/uploads/2016/05/shutterstock_131482376.jpg"

        ))

        audioFiles.add(Song(
                "4",
                "Cross-Section of a Living Man",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/Cross-Section%20of%20a%20Living%20Man%20%20Creepypasta.mp3?alt=media&token=c1b338d6-b8c8-4764-9a65-dc3ce92dd6cc",
                "https://media.newyorker.com/photos/5bd78600daf42d0501bd51e6/16:9/w_1280,c_limit/Treisman-Scary-Stories.jpg"

        ))

        audioFiles.add(Song(
                "5",
                "Don't Eat Today's Special",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/Don't%20Eat%20Today's%20Special%20%20Creepypasta.mp3?alt=media&token=60744e77-a360-4ea9-9aac-f1ada0146850",
                "https://advancelocal-adapter-image-uploads.s3.amazonaws.com/image.pennlive.com/home/penn-media/width2048/img/patriot-news/photo/2015/08/28/18643904-standard.jpg"

        ))

        audioFiles.add(Song(
                "6",
                "Hearts of the Young",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/Hearts%20of%20the%20Young%20%20Creepypasta.mp3?alt=media&token=589bf9e7-c343-4ca7-8ec3-6d4486191106",
                "https://i.pinimg.com/originals/89/13/d5/8913d56494154e425e90d01432cd6583.jpg"

        ))

        audioFiles.add(Song(
                "7",
                "I can't remember if I have a wife or not",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/I%20can't%20remember%20if%20I%20have%20a%20wife%20or%20not%20%20Creepypasta.mp3?alt=media&token=1f69f3f7-471a-4cba-b9bf-5cf73d9c90af",
                "https://www.orissapost.com/wp-content/uploads/2019/01/brooding_man.jpg"

        ))

        audioFiles.add(Song(
                "8",
                "I clicked one of those spam pop-ups as a kid",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/I%20clicked%20one%20of%20those%20spam%20pop-ups%20as%20a%20kid%20%20Creepypasta.mp3?alt=media&token=158a206d-3200-4131-ab2e-264283bf9835",
                "https://preview.redd.it/d5xd2bcyzfw41.jpg?auto=webp&s=131e5666ac1561e20930df625d09d17258fb0de8"

        ))

        audioFiles.add(Song(
                "9",
                "I taught an AI to talk on Facebook",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/I%20taught%20an%20AI%20to%20talk%20on%20Facebook%20%20Creepypasta.mp3?alt=media&token=0b34880d-0df3-4329-94a4-b7ce6311a69c",
                "https://miro.medium.com/max/2800/1*zwLxp5lR17BT2C_vs7HCsQ.jpeg"

        ))

        audioFiles.add(Song(
                "10",
                "Kairos",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/Kairos%20%20Creepypasta.mp3?alt=media&token=73dae322-cc1f-4a52-ac5d-636fbb0e2990",
                "https://i.pinimg.com/originals/10/58/ad/1058adc39d5a9cb25df002963c484e39.jpg"

        ))

        audioFiles.add(Song(
                "11",
                "Lost and Found",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/Lost%20and%20Found%20%20Creepypasta.mp3?alt=media&token=4da54431-0f0e-48b5-9d66-927304fa76d6",
                "https://i.pinimg.com/originals/10/06/4e/10064e517662ba2a34900a39f7d8c5b7.jpg"

        ))

        audioFiles.add(Song(
                "12",
                "New City Village",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/New%20City%20Village%20%20Creepypasta.mp3?alt=media&token=0c23e649-af54-43ff-a6ad-66e75e41532a",
                "https://i.redd.it/eevlmyrb5ny11.png"

        ))

        audioFiles.add(Song(
                "13",
                "Some Toys Aren't Meant To Be Played With",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/Some%20Toys%20Aren't%20Meant%20To%20Be%20Played%20With%20%20Creepypasta.mp3?alt=media&token=957d93c0-5165-4b14-b7a7-309cda7a7477",
                "https://i.pinimg.com/236x/74/79/53/747953f8fef1cc8707d4eb3b791f08e0--haunted-dolls-haunted-halloween.jpg"

        ))

        audioFiles.add(Song(
                "14",
                "St. Anne's Mercy",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/St.%20Anne's%20Mercy%20%20Creepypasta.mp3?alt=media&token=ef646ff3-84fc-4ddb-924d-45755a07d9da",
                "https://media-cdn.tripadvisor.com/media/photo-s/03/22/7f/ca/oldchurch-house-b-b.jpg"

        ))

        audioFiles.add(Song(
                "15",
                "The Lights",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/The%20Lights%20%20Creepypasta.mp3?alt=media&token=534c8be6-dd78-4729-8d4f-b9cf02283497",
                "https://www.theforestdark.com/wordpress/wp-content/uploads/2015/10/IMG_5858.jpg"

        ))

        audioFiles.add(Song(
                "16",
                "The Man and the Puppet",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/The%20Man%20and%20the%20Puppet%20%20Creepypasta.mp3?alt=media&token=2c9dfa92-b4e9-4223-ac79-e9141906abad",
                "https://cdn.vox-cdn.com/thumbor/hXiOqkpj-b7gmx7WaRUe6_NbWpE=/1400x1400/filters:format(jpeg)/cdn.vox-cdn.com/uploads/chorus_asset/file/13467218/Sabrina_14.jpg"

        ))

        audioFiles.add(Song(
                "17",
                "The Quiet Ones",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/The%20Quiet%20Ones%20%20Creepypasta.mp3?alt=media&token=d647a24b-379b-4aa5-abd1-0d19222bcdec",
                "https://mtv.mtvnimages.com/uri/mgid:ao:image:mtv.com:155477?quality=0.8&format=jpg&width=1440&height=810&.jpg"

        ))

        audioFiles.add(Song(
                "18",
                "The Solitude of Connor",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/The%20Solitude%20of%20Connor%20%20Creepypasta.mp3?alt=media&token=7243a4ed-b408-4adc-812b-bab48d86f1bc",
                "https://www.incimages.com/uploaded_files/image/1920x1080/getty_866260356_335372.jpg"

        ))

        audioFiles.add(Song(
                "19",
                "Why Babies are Born Screaming",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/Why%20Babies%20are%20Born%20Screaming%20%20Creepypasta.mp3?alt=media&token=7968fedb-59de-4f42-b591-e7ec61da13fb",
                "https://i.ytimg.com/vi/H7VbADyswUQ/maxresdefault.jpg"

        ))

        audioFiles.add(
            Song(
                "20",
                "You'll Never Believe",
                "CreepyPasta",
                "https://firebasestorage.googleapis.com/v0/b/scared-6b4bc.appspot.com/o/You'll%20Never%20Believe%20What%20Happened%20to%20Me%20at%20Work%20Yesterday%20%20Creepypasta.mp3?alt=media&token=b1dfbd4e-b827-43d1-b252-bc8a2dac8457",
                "https://media.moddb.com/images/games/1/39/38962/steamworkshop_webupload_previewfile_394789003_preview_1.jpg"

            )
        )


        for (audio in audioFiles) {
            db.collection("audio").document(audio.title).set(audio)
        }
    }

    private fun uploadVideos() {
        val db = FirebaseFirestore.getInstance()

        val videos: ArrayList<Video> = ArrayList()


        videos.add(
            Video(
                "NpGpiZfor0I",
                "Ex-Prisoners Scary stories",
                "Ex-Prisoners share their SCARIEST prison stories (r/AskReddit)",
                "https://i.ytimg.com/vi/TkzwXjc-80A/maxresdefault.jpg"
            )
        )



        for (video in videos) {

            db.collection("videos")
                .document("reddit")
                .collection("2020")
                .document(video.title).set(video)

        }


    }
}
