package com.example.uchet

import com.example.uchet.Dao.AccDao
import com.example.uchet.Dao.DepartureDao
import com.example.uchet.Dao.DocumentDao
import com.example.uchet.Dao.SotrInDocDao
import com.example.uchet.Dao.SotrudnikDao
import com.example.uchet.Dao.TransportDao
import com.example.uchet.entities.Acc
import com.example.uchet.entities.Departure
import com.example.uchet.entities.Document
import com.example.uchet.entities.Sotrudnik
import com.example.uchet.entities.SotrudnikiInDocument
import com.example.uchet.entities.Transport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class Repository(
    private val documentDao: DocumentDao,
    private val accDao: AccDao,
    private val departureDao: DepartureDao,
    private val transportDao: TransportDao,
    private val sotrudnikDao: SotrudnikDao,
    private val sotrInDocDao: SotrInDocDao,
) {
    private val _cardValue = MutableStateFlow("")
    val cardValue: StateFlow<String> get() = _cardValue

    private val _page = MutableStateFlow("")
    val page: StateFlow<String> get() = _page
    fun changePage(s: String) {
        _page.value = s
    }
    fun changeCardValue(s: String) {
        _cardValue.value = s
    }
    //Document
    val readAllDoc = documentDao.getAll()

    fun readAllDocByDate(date: Date): Flow<List<Document>> {
        return documentDao.getDocsByDate(date)
    }
    suspend fun getDocById(id: Int) = documentDao.get(id)
    //suspend fun getDocId(document: Document) = documentDao.getId(document.distribution,document.departure_date,document.transport,document.venues)
    suspend fun insertDocument(document: Document) {
        documentDao.insert(document)
    }
    suspend fun deleteAllDocument() {
        documentDao.deleteAll()
    }
    suspend fun getDocRow():Int{
        return documentDao.getRowCount()
    }

    //Sotrudnik
    val readAllSotrudnik = sotrudnikDao.getAll()
    suspend fun insertSotrudnik(sotrudnik: Sotrudnik) {
        sotrudnikDao.insert(sotrudnik)
    }
    suspend fun deleteAllSotrudnik() {
        sotrudnikDao.deleteAll()
    }
    suspend fun getSotrRow():Int{
        return sotrudnikDao.getRowCount()
    }
    suspend fun getByUID(uid:Long): Sotrudnik?{
        return sotrudnikDao.getByUID(uid)
    }
    suspend fun getByID(id:String): Sotrudnik?{
        return sotrudnikDao.getByID(id)
    }

    suspend fun getIds(i: Int): List<String>{
        return sotrudnikDao.getIds(i)
    }

    suspend fun insertSotrudniks(sotrudnik: List<Sotrudnik>) = sotrudnikDao.insertAll(sotrudnik)

    //Sotrudnik in document
    fun readAllSotrInDoc(id:Int) = sotrInDocDao.getById(id)

    fun readTest(id: Int) = sotrInDocDao.getAllSotrInDoc(id)
    fun readTest2(id: Int) = sotrInDocDao.getAllSotrInDocToOtchet(id)
    suspend fun insertSotrInDoc(sotrudnikiInDocument: SotrudnikiInDocument) {
        sotrInDocDao.insert(sotrudnikiInDocument)
    }
    suspend fun getSotrInDoc() {
        sotrInDocDao.deleteAll()
    }
    suspend fun getSotrInDocRow():Int{
        return sotrInDocDao.getRowCount()
    }
    suspend fun changeMark(mark: Boolean, id: String){
        return sotrInDocDao.changeMark(mark,id)
    }
    suspend fun clearMarks(id: Int){
        return sotrInDocDao.clearMarks(id)
    }
    suspend fun delete(id: Int){
        return sotrInDocDao.deleteById(id)
    }
    suspend fun deleteSotrInDoc(id: String){
        return sotrInDocDao.deleteSotrInDoc(id)
    }
    suspend fun updateVenue(id: String, id_venue:Int){
        return sotrInDocDao.updateVenue(id,id_venue)
    }
    suspend fun getSotrInDoc(uid: Long): Int{
        return sotrInDocDao.getByUid(uid)
    }
    suspend fun deleteAllSotrInDoc(){
        return sotrInDocDao.deleteAll()
    }

    //Departure
    suspend fun readAllDeparture() = departureDao.getAll()
    suspend fun insertDeparture(departure: Departure) {
        departureDao.insert(departure)
    }
    suspend fun deleteAllDeparture() {
        departureDao.deleteAll()
    }
    suspend fun getDepartureRow():Int{
        return departureDao.getRowCount()
    }
    suspend fun getDeparture(id:Int):String{
        return departureDao.get(id)
    }

    //Transport
    suspend fun readAllTransport() = transportDao.getAll()
    suspend fun insertTransport(transport: Transport) {
        transportDao.insert(transport)
    }
    suspend fun deleteAllTransport() {
        transportDao.deleteAll()
    }
    suspend fun getTransportRow():Int{
        return transportDao.getRowCount()
    }
    suspend fun getTransportNameById(id: Int):String{
        return transportDao.getName(id)
    }

    //Account
    suspend fun getAccRow():Int{
        return accDao.getRowCount()
    }
    suspend fun checkAcc(login: String, password: String): Int{
        return accDao.checkAcc(login,password)
    }
    suspend fun insertAcc(acc: Acc){
        accDao.insertAccount(acc)
    }
    suspend fun getAccByPass(uid: String): Int{
        return accDao.getAccByPass(uid)
    }
    val readAllAcc = accDao.getAllAccount()
    suspend fun delAcc(acc: Acc){
        return accDao.deleteAccount(acc)
    }
}