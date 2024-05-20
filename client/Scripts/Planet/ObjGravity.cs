using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ObjGravity : MonoBehaviour
{
    public Transform planet; // 행성의 Transform 컴포넌트
    public float gravityStrength = 9.8f; // 중력의 세기

    private Rigidbody rb; // Rigidbody 컴포넌트

    void Start()
    {
        rb = GetComponent<Rigidbody>(); // Rigidbody 컴포넌트 찾기
        FindAndSetNearestPlanet(); // 가장 가까운 행성 찾아서 설정
    }

    void FixedUpdate()
    {
        Vector3 direction = (planet.position - transform.position).normalized;
        Vector3 gravity = direction * gravityStrength;
        rb.AddForce(gravity, ForceMode.Acceleration);
    }

    void FindAndSetNearestPlanet()
    {
        float minDistance = float.MaxValue;
        Planet closestPlanet = null;

        foreach (var obj in GameObject.FindObjectsOfType<Planet>()) // 모든 Planet 객체를 찾음
        {
            float distance = Vector3.Distance(transform.position, obj.transform.position);
            if (distance < minDistance)
            {
                minDistance = distance;
                closestPlanet = obj;
            }
        }

        if (closestPlanet != null)
        {
            planet = closestPlanet.transform;
        }
    }
}