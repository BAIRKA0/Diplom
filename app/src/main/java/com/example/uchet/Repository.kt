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

class Repository(
    private val documentDao: DocumentDao,
    private val accDao: AccDao,
    private val departureDao: DepartureDao,
    private val transportDao: TransportDao,
    private val sotrudnikDao: SotrudnikDao,
    private val sotrInDocDao: SotrInDocDao,
) {
    //Document
    val readAllDoc = documentDao.getAll()
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

    //Sotrudnik in document
    fun readAllSotrInDoc(id:Int) = sotrInDocDao.getById(id)
    suspend fun insertSotrInDoc(sotrudnikiInDocument: SotrudnikiInDocument) {
        sotrInDocDao.insert(sotrudnikiInDocument)
    }
    suspend fun getSotrInDoc() {
        sotrInDocDao.deleteAll()
    }
    suspend fun getSotrInDocRow():Int{
        return sotrInDocDao.getRowCount()
    }
    suspend fun changeMark(mark: Boolean, id: Int){
        return sotrInDocDao.changeMark(mark,id)
    }
    suspend fun clearMarks(id: Int){
        return sotrInDocDao.clearMarks(id)
    }
    suspend fun delete(id: Int){
        return sotrInDocDao.deleteById(id)
    }
    suspend fun updateVenue(id: Int, id_venue:Int){
        return sotrInDocDao.updateVenue(id,id_venue)
    }
    suspend fun getSotrInDoc(uid: Long): Int{
        return sotrInDocDao.getByUid(uid)
    }

    //Departure
    suspend fun readAllDeparture() = departureDao.getAll()
    suspend fun insertDeparture(departure: Departure) {
        departureDao.insert(departure)
    }
    suspend fun deleteAllDeparture(departure: Departure) {
        departureDao.delete(departure)
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
    suspend fun deleteAllTransport(transport: Transport) {
        transportDao.delete(transport)
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
}