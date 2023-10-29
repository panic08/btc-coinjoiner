import './App.css';
import Header from "./components/header/Header";
import Main from "./components/main/Main";
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Footer from "./components/footer/Footer";
import {useEffect} from "react";
import Cookie from "js-cookie"
import {useDispatch} from "react-redux";
import Account from "./components/account/Account";
import Authorization from "./components/authorization/Authorization";
import {AuthInfo} from "./requests";

const App = () => {

    const dispatch = useDispatch();

    useEffect(() => {
        if(Cookie.get("jwt")){
            dispatch({type: "SET_IS_AUTH", isAuth: true})
        }
    }, []);



  return (
      <div className="App">
    <BrowserRouter>
        <Header/>
        <div className="container">
            <Routes>
                <Route path="/" element={<Main/>}/>
                <Route path="/account" element={<Account/>}/>
                <Route path="/authorization" element={<Authorization/>}/>
            </Routes>
        </div>
        <Footer/>
    </BrowserRouter>
      </div>
  );
}

export default App;
