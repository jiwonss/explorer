using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class RayCast : MonoBehaviour
{
    public float maxDistance = 15f; 

    void Update()
    {
        if (Input.GetMouseButtonDown(0))
        {

             Debug.DrawRay(transform.position, transform.forward * maxDistance, Color.red);

            RaycastHit hit;
            if(Physics.Raycast(transform.position, transform.forward, out hit, maxDistance))
            {
                Debug.Log(hit.collider.gameObject.name);
            }   
            
        }
    }
}
