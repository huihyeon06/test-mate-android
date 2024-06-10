package kr.hs.emirim.evie.testmateloginpage.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.hs.emirim.evie.testmateloginpage.calendar.Calendar
import com.github.mikephil.charting.charts.LineChart
import kr.hs.emirim.evie.testmateloginpage.subject.GoalMainListActivity
import kr.hs.emirim.evie.testmateloginpage.R
import kr.hs.emirim.evie.testmateloginpage.wrong_answer_note.WrongAnswerListActivity
import kr.hs.emirim.evie.testmateloginpage.databinding.ActivityHomeBinding
import kr.hs.emirim.evie.testmateloginpage.home.data.TestData
import kr.hs.emirim.evie.testmateloginpage.login.CurrentUser
import kr.hs.emirim.evie.testmateloginpage.subject.AddSubjectActivity
import kr.hs.emirim.evie.testmateloginpage.subject.BOOK_TAG
import kr.hs.emirim.evie.testmateloginpage.subject.SUBJECT_NAME
import kr.hs.emirim.evie.testmateloginpage.subject.SubjectHomeAdapter
import kr.hs.emirim.evie.testmateloginpage.subject.SubjectViewModel
import kr.hs.emirim.evie.testmateloginpage.subject.SubjectViewModelFactory
import kr.hs.emirim.evie.testmateloginpage.subject.data.Subject
import kr.hs.emirim.evie.testmateloginpage.subject.data.SubjectRequest
import kr.hs.emirim.evie.testmateloginpage.util.SpinnerUtil
import kr.hs.emirim.evie.testmateloginpage.wrong_answer_note.WrongAnswerListViewModel
import kr.hs.emirim.evie.testmateloginpage.wrong_answer_note.WrongAnswerListViewModelFactory

class HomeActivity : AppCompatActivity() {
    // 시험기록 데이터 생성
    val testRecordDataList: List<TestData> = listOf(
        TestData("1학년 2학기 기말", 75),
        TestData("1학년 2학기 중간", 60),
        TestData("1학년 1학기 기말", 80),
        TestData("1학년 1학기 중간", 100),
        TestData("@학년 @학기 중간", 89),
        TestData("@학년 @학기 중간", 91),
        TestData("@학년 @학기 중간", 78),
        TestData("@학년 @학기 중간", 96)
    )

    lateinit var navHome: ImageButton
    lateinit var navGoal: ImageButton
    lateinit var navCal: ImageButton

    lateinit var navWrong: ImageButton

    lateinit var addSubjectBtn: ImageButton
    lateinit var editTestRecordBtn: ImageButton

    lateinit var userGrade: TextView
    lateinit var spinner: Spinner

    lateinit var toggle: ImageButton

    private val newSubjectActivityRequestCode = 1
    private val subjectsListViewModel by viewModels<SubjectViewModel> {
        SubjectViewModelFactory(this)
    }

    private val listViewModel by viewModels<WrongAnswerListViewModel> {
        WrongAnswerListViewModelFactory(this)
    }

    private lateinit var binding: ActivityHomeBinding

    //    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 성적 그래프
        val linechart = findViewById<LineChart>(R.id.home_test_record_chart)
        val horizontalScrollView = findViewById<HorizontalScrollView>(R.id.home_scroll_view_graph)
        val scoreChart = ScoreChart(linechart, horizontalScrollView, this)
        scoreChart.setupChart(testRecordDataList)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

//       toggle = findViewById(R.id.toggle)
//       drawerLayout = findViewById(R.id.drawer_layout)
//       toggle.setOnClickListener {
//           drawerLayout.openDrawer(GravityCompat.START)
//
//       }

        spinner = SpinnerUtil.gradeSpinner(this, R.id.spinnerWrong)
        spinner.setSelection(CurrentUser.userDetails!!.grade.toInt() - 1)
        var selectedPosition = spinner.selectedItemPosition// grade 인덱스 (ex. 3)
        var selectedItem =
            spinner.getItemAtPosition(selectedPosition).toString() // grade 문자열 (ex. 고등학교 2학년)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // 선택된 항목의 위치(position)를 이용하여 해당 항목의 값을 가져옴
                selectedPosition = position + 1

                listViewModel.readNoteList(selectedPosition, 1)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 것도 선택되지 않았을 때 처리할 작업
            }
        }

        // subjectRecyclerView
        val subjectsAdapter = SubjectHomeAdapter { subject -> adapterOnClick(subject) } // TODO
        val recyclerView: RecyclerView = findViewById(R.id.subjectRecyclerView)

        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) // 수평 레이아웃 방향 설정
        recyclerView.adapter = subjectsAdapter

        subjectsListViewModel.subjectListData.observe(
            // observer : 어떤 이벤트가 일어난 순간, 이벤트를 관찰하던 관찰자들이 바로 반응하는 패턴
            this
        ) {
            it?.let { // goalsLiveData의 값이 null이 아닐 때 중괄호 코드 실행
                subjectsAdapter.submitList(it as MutableList<Subject>) // 어댑터 내의 데이터를 새 리스트로 업데이트하는 데 사용
            }
        }

        // 과목 추가
        addSubjectBtn = findViewById(R.id.addSubjectBtn)
        addSubjectBtn.setOnClickListener {
            val intent = Intent(this@HomeActivity, AddSubjectActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//            startActivity(intent)
            startActivityForResult(intent, newSubjectActivityRequestCode)
        }
//       binding.addSubjectBtn.setOnClickListener {
//           val dialog = AddSubjectActivity()
//           dialog.show(supportFragmentManager, "CustomDialog")
//       }

        editTestRecordBtn = findViewById(R.id.edit_test_record_btn)
        editTestRecordBtn.setOnClickListener {
            val intent = Intent(this@HomeActivity, EditTestRecordActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//            startActivity(intent)
            startActivityForResult(intent, newSubjectActivityRequestCode)
        }


        navHome = findViewById(R.id.nav_home)
        navWrong = findViewById(R.id.nav_wrong)
        navGoal = findViewById(R.id.nav_goal)
        navCal = findViewById(R.id.nav_cal)

        navHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent)
        }
        navWrong.setOnClickListener {
            val intent = Intent(this, WrongAnswerListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent)
        }
        navGoal.setOnClickListener {
            val intent = Intent(this, GoalMainListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent)
        }
        navCal.setOnClickListener {
            val intent = Intent(this, Calendar::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent)
        }
    } // onCreate

    private fun adapterOnClick(subject: Subject) {
        // TODO : 과목별 화면으로 이동
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
//        super.onActivityResult(requestCode, resultCode, intentData)
//
//        /* Inserts subject into viewModel. */
//        if (resultCode == Activity.RESULT_OK) {
//            intentData?.let { data ->
//                val subjectName = data.getStringExtra(SUBJECT_NAME)
//                val subjectImage = data.getStringExtra(BOOK_TAG)
//
//                val newSubject = Subject(
//                    CurrentUser.userDetails!!.grade, subjectName, subjectImage
//                )
//
//                subjectsListViewModel.addList(newSubject) ///////////////////////////////////////// insertFlower
//            }
//        }
//    }
}