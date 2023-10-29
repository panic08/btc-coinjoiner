import React, {useEffect, useState} from 'react';
import './Main.css'
import bitcoin from '../../images/bitcoin.png'
import vk from "../../images/vk.png";
import close from "../../images/close.png"
import Cookie from "js-cookie";
import {AuthInfo, URL} from '../../requests'
import {useDispatch} from "react-redux";

const Main = () => {

    const dispatch = useDispatch();
    const jwt = Cookie.get('jwt');

    const [titleText, setTitleText] = useState("правдивых");
    const [index, setIndex] = useState(0);
    const dict = ["быстрых", "крутых", "суперских", "верных", "правдивых"]

    useEffect(() => {
        setTimeout(() => {
            if (index < dict.length) {
                setTitleText(dict[index]);
                setIndex(index + 1);
            } else {
                setIndex(1);
                setTitleText(dict[0]);
            }
        }, 1000);

    }, [index, titleText]);

    useEffect(() => {
        if (Cookie.get('jwt')) {
            AuthInfo(jwt).then(response => {
                if (response.status === 401) {
                    throw new Error();
                }
            }).catch((e) => {
                dispatch({type: "SET_IS_AUTH", isAuth: false});
                Cookie.remove('jwt');
            })
        }
    }, []);

    const handleCloseClick = () => {
        const element = document.querySelector('.form-container');
        element.classList.remove('active');
    }


    return (
        <>
            <div className="main">
                <div className="bitcoin">
                    <img src={bitcoin} alt=""/>
                </div>
                <div className="title">
                    <div className="big-title">Совершение <br/>
                        {titleText} коинджоин
                        <br/>BTC транкзакций
                    </div>
                    <div className="small-title">Совершайте быстрые COIN-JOIN
                        транзакции соединяя колонки
                    </div>
                </div>
            </div>
            <div className="form-container">
                <div className="form">
                    <div className="form-content">
                        <div className="form-title">
                            <div className="form-title-text">Авторизация</div>
                            <div className="form-title-image" onClick={handleCloseClick}><img width="20px" height="20px"
                                                                                              src={close} alt=""/></div>
                        </div>
                        <div className="form-content-main">
                            <a href={URL + "/api/auth/oauth/redirectVk"} className="vk">
                                <img src={vk} width="24px" height="24px" alt=""/>
                            </a>
                            <div className="label">
                                <input type="checkbox" className="form-checkbox"/>
                                <div>Я принимаю <span>условия пользования</span></div>
                            </div>
                        </div>
                        <div className="form-footer">
                            Данный сайт защищен reCAPTCHA с соответствующей политикой конфиденциальности Google <span>Политика
                            конфиденциальности</span> и условиями предоставления услуг <span>Условия предоставления услуг</span>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
};

export default Main;