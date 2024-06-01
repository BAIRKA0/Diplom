package com.example.uchet.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uchet.Graph
import com.example.uchet.Repository
import com.example.uchet.entities.Acc
import com.example.uchet.entities.Departure
import com.example.uchet.entities.Document
import com.example.uchet.entities.Sotrudnik
import com.example.uchet.entities.SotrudnikiInDocument
import com.example.uchet.entities.Transport
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Random

class AuthViewModel(
    private val repository: Repository = Graph.repository
) :ViewModel() {
    init {
        viewModelScope.launch {
            if(repository.getAccRow()==0){
                repository.insertAcc(Acc("l","p","123"))
            }
            if(repository.getSotrRow()==0){
                val name = listOf("Юрий","Даниил","Валера","Андрей","Денис","Егор","Михаил","Евгений","Дмитрий","Николай","Никита","Иван","Павел")
                val surname = listOf("Жалсанов","Афанасьев","Гунов","Сергеев","Шабаев","Астахаев","Житов","Корытов","Богачев","Погудин","Король")
                val patronymic = listOf("Витальевич","Андреевич","Юрьевич","Даниилович","Сергеевич","Денисович","Эдуардович","Павлович","Егорович","Дмитриевич","Николаевич")
                repository.insertSotrudnik(
                    Sotrudnik(
                        name = "Юрий",
                        surname = "Жалсанов",
                        patronymic = "Витальевич",
                        company = "ИНК",
                        uid = 123456789
                    )
                )
                repository.insertSotrudnik(
                    Sotrudnik(
                        name = "Юрий",
                        surname = "Жалсанов",
                        patronymic = "Витальевич",
                        company = "Компания 2",
                        uid = 123456788
                    )
                )
                for (i in 1..30) {
                    repository.insertSotrudnik(
                        Sotrudnik(
                            name = name[Random().nextInt(name.size)],
                            surname = surname[Random().nextInt(surname.size)],
                            patronymic = patronymic[Random().nextInt(patronymic.size)],
                            company = "ИНК",
                            uid = (123456700+i.toLong())
                        )
                    )
                }
                repository.insertSotrudnik(
                    Sotrudnik(
                        name = "Юрий",
                        surname = "Жалсанов",
                        patronymic = "Витальевич",
                        company = "ИНК",
                        uid = 123456787
                    )
                )
            }
            if(repository.getDepartureRow()==0){
                for (i in 1..20){
                    repository.insertDeparture(
                        Departure(
                        name = "Точка "+i
                    )
                    )
                }
            }
            if(repository.getTransportRow()==0){
                for (i in 1..10){
                    repository.insertTransport(
                        Transport(
                        name = "Транспорт "+i
                    )
                    )
                }
            }
            if(repository.getDocRow()==0){
                for (i in 1..30) {
                    repository.insertDocument(
                        Document(
                            distribution = "Распределение " + i,
                            departure_date = Date(2024-1900,4,i),
                            id_transport = Random().nextInt(10)+1,
                            id_destination = Random().nextInt(20)+1,
                            id_venues = (Random().nextInt(20)+1).toString()+","+(Random().nextInt(20)+1)+","+(Random().nextInt(20)+1)
                        )
                    )
                }
                repository.insertDocument(
                    Document(
                        id = 0,
                        distribution = "Без распределения",
                        departure_date = Date(2024-1900,2-1,9),
                        id_transport = 1,
                        id_destination = 1,
                        id_venues = "1,2,3"
                    )
                )
            }
            if(repository.getSotrInDocRow()==0){
                for (i in 1..10) {
                    for (i in 1..10) {
                        repository.insertSotrInDoc(
                            SotrudnikiInDocument(
                            id_sotrudnik = Random().nextInt(30)+1,
                            id_doc = Random().nextInt(10)+1,
                            id_venue_in_doc = Random().nextInt(20)+1,
                            id_venue_fact = 0,
                            route = "Рейс ",
                            mark = 0,
                            available_in_doc = 1
                        )
                        )
                    }
                }
            }
        }
    }

    suspend fun auth(login: String,password: String): Boolean{
        if(repository.checkAcc(login, password)>0){
            return true
        } else return false
    }
}